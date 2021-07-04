package org.ethereum.lists.chains

import com.beust.klaxon.JsonArray
import java.io.File
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import org.ethereum.lists.chains.model.*
import org.kethereum.erc55.isValid
import org.kethereum.model.Address
import org.kethereum.rpc.HttpEthereumRPC

val parsedShortNames = mutableSetOf<String>()
val parsedNames = mutableSetOf<String>()

val iconsPath = File("_data/icons")

val chainsPath = File("_data/chains")
private val allFiles = chainsPath.listFiles() ?: error("$chainsPath must contain the chain json files - but it does not")
private val allChainFiles = allFiles.filter { !it.isDirectory }

fun main(args: Array<String>) {

    doChecks(doRPCConnect = args.contains("rpcConnect"))

    createOutputFiles()
}

private fun createOutputFiles() {
    val buildPath = File("output")
    buildPath.mkdir()

    val fullJSONFile = File(buildPath, "chains.json")
    val prettyJSONFile = File(buildPath, "chains_pretty.json")
    val miniJSONFile = File(buildPath, "chains_mini.json")
    val prettyMiniJSONFile = File(buildPath, "chains_mini_pretty.json")

    val chainJSONArray = JsonArray<JsonObject>()
    val miniChainJSONArray = JsonArray<JsonObject>()

    allChainFiles.forEach {
        val jsonObject = Klaxon().parseJsonObject(it.reader())
        chainJSONArray.add(jsonObject)
        fullJSONFile.writeText(chainJSONArray.toJsonString())
        prettyJSONFile.writeText(chainJSONArray.toJsonString(prettyPrint = true))

        val miniJSON = JsonObject()
        listOf("name", "chainId", "shortName", "networkId", "nativeCurrency", "rpc", "faucet", "infoURL").forEach { field ->
            jsonObject[field]?.let { content ->
                miniJSON[field] = content
            }
        }
        miniChainJSONArray.add(miniJSON)

        miniJSONFile.writeText(miniChainJSONArray.toJsonString())
        prettyMiniJSONFile.writeText(miniChainJSONArray.toJsonString(prettyPrint = true))
    }

    File(buildPath, "index.html").writeText(
        """
            <!DOCTYPE HTML>
            <html lang="en-US">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="refresh" content="0; url=https://chainlist.org">
                    <script type="text/javascript">
                        window.location.href = "https://chainlist.org"
                    </script>
                    <title>Page Redirection</title>
                </head>
                <body>
                    If you are not redirected automatically, follow this <a href='https://chainlist.org'>link to chainlist.org</a>.
                </body>
            </html>
    """.trimIndent()
    )
}

private fun doChecks(doRPCConnect: Boolean) {
    allChainFiles.forEach {
        checkChain(it, doRPCConnect)
    }

    val allIcons = iconsPath.listFiles() ?: return
    allIcons.forEach {
        checkIcon(it)
    }

    allFiles.filter { it.isDirectory }.forEach {
        if (it.name != "deprecated") {
            error("the only directory allowed is 'deprecated'")
        }
    }
}

fun checkIcon(icon: File) {
    println("checking Icon " + icon.name)
    val obj: JsonArray<*> = Klaxon().parseJsonArray(icon.reader())
    println("found variants " + obj.size)
    obj.forEach { it ->
        if (it !is JsonObject) {
            error("Icon variant must be an object")
        }

        val url = it["url"] ?: error("Icon must have a URL")

        if (url !is String || !url.startsWith("ipfs://")) {
            error("url must start with ipfs://")
        }

        val width = it["width"]
        val height = it["height"]
        if (width != null || height != null) {
            if (height == null || width == null) {
                error("If icon has width or height it needs to have both")
            }

            if (width !is Int) {
                error("Icon width must be Int")
            }
            if (height !is Int) {
                error("Icon height must be Int")
            }
        }

        val format = it["format"]
        if (format !is String || (format != "png" && format != "svg")) {
            error("Icon format must be a png or svg but was $format")
        }
    }
}

fun checkChain(chainFile: File, connectRPC: Boolean) {
    println("processing $chainFile")

    parseWithMoshi(chainFile)

    val jsonObject = Klaxon().parseJsonObject(chainFile.reader())
    val chainAsLong = getNumber(jsonObject, "chainId")

    if (chainFile.nameWithoutExtension.startsWith("eip155-")) {
        if (chainAsLong != chainFile.nameWithoutExtension.replace("eip155-", "").toLongOrNull()) {
            throw(FileNameMustMatchChainId())
        }
    } else {
        throw(UnsupportedNamespace())
    }

    if (chainFile.extension != "json") {
        throw(ExtensionMustBeJSON())
    }

    getNumber(jsonObject, "networkId")

    val extraFields = jsonObject.map.keys.subtract(mandatory_fields).subtract(optionalFields)
    if (extraFields.isNotEmpty()) {
        throw ShouldHaveNoExtraFields(extraFields)
    }

    val missingFields = mandatory_fields.subtract(jsonObject.map.keys)
    if (missingFields.isNotEmpty()) {
        throw ShouldHaveNoMissingFields(missingFields)
    }

    jsonObject["icon"]?.let {
        if (!File(iconsPath, "$it.json").exists()) {
            error("The Icon $it does not exist - was used in ${chainFile.name}")
        }
    }

    jsonObject["explorers"]?.let {
        if (it !is JsonArray<*>) {
            throw (ExplorersMustBeArray())
        }

        it.forEach { explorer ->
            if (explorer !is JsonObject) {
                error("explorer must be object")
            }

            if (explorer["name"] == null) {
                throw(ExplorerMustHaveName())
            }

            val url = explorer["url"]
            if (url == null || url !is String || !url.startsWith("https://")) {
                throw(ExplorerInvalidUrl())
            }

            if (explorer["standard"] != "EIP3091") {
                throw(ExplorerStandardMustBeEIP3091())
            }
        }
    }
    jsonObject["ens"]?.let {
        if (it !is JsonObject) {
            throw ENSMustBeObject()
        }
        if (it.keys != mutableSetOf("registry")) {
            throw ENSMustHaveOnlyRegistry()
        }

        val address = Address(it["registry"] as String)
        if (!address.isValid()) {
            throw ENSRegistryAddressMustBeValid()
        }
    }

    if (connectRPC) {
        if (jsonObject["rpc"] is List<*>) {
            (jsonObject["rpc"] as List<*>).forEach {
                if (it !is String) {
                    throw(RPCMustBeListOfStrings())
                } else {
                    println("connecting to $it")
                    val ethereumRPC = HttpEthereumRPC(it)
                    println("Client:" + ethereumRPC.clientVersion())
                    println("BlockNumber:" + ethereumRPC.blockNumber())
                    println("GasPrice:" + ethereumRPC.gasPrice())
                }
            }
            println()
        } else {
            throw(RPCMustBeList())
        }
    }
}

/*
moshi fails for extra commas
https://github.com/ethereum-lists/chains/issues/126
*/
private fun parseWithMoshi(fileToParse: File) {
    val parsedChain = chainAdapter.fromJson(fileToParse.readText())
    if (parsedNames.contains(parsedChain!!.name)) {
        throw NameMustBeUnique(parsedChain.name)
    }
    parsedNames.add(parsedChain.name)

    if (parsedShortNames.contains(parsedChain.shortName)) {
        throw ShortNameMustBeUnique(parsedChain.shortName)
    }
    parsedShortNames.add(parsedChain.shortName)
}

private fun getNumber(jsonObject: JsonObject, field: String): Long {
    return when (val chainId = jsonObject[field]) {
        is Int -> chainId.toLong()
        is Long -> chainId
        else -> throw(Exception("chain_id must be a number"))
    }
}