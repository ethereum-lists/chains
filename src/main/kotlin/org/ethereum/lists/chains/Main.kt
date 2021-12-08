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
    val buildPath = File("output").apply { mkdir() }

    val chainJSONArray = JsonArray<JsonObject>()
    val miniChainJSONArray = JsonArray<JsonObject>()
    val shortNameMapping = JsonObject()

    allChainFiles
        .map { Klaxon().parseJsonObject(it.reader()) }
        .sortedBy { (it["chainId"] as Number).toLong() }
        .forEach { jsonObject ->
            chainJSONArray.add(jsonObject)


            val miniJSON = JsonObject()
            listOf("name", "chainId", "shortName", "networkId", "nativeCurrency", "rpc", "faucets", "infoURL").forEach { field ->
                jsonObject[field]?.let { content ->
                    miniJSON[field] = content
                }
            }
            miniChainJSONArray.add(miniJSON)

            shortNameMapping[jsonObject["shortName"] as String] = "eip155:" + jsonObject["chainId"]

        }

    File(buildPath, "chains.json").writeText(chainJSONArray.toJsonString())
    File(buildPath, "chains_pretty.json").writeText(chainJSONArray.toJsonString(prettyPrint = true))

    File(buildPath, "chains_mini.json").writeText(miniChainJSONArray.toJsonString())
    File(buildPath, "chains_mini_pretty.json").writeText(miniChainJSONArray.toJsonString(prettyPrint = true))

    File(buildPath, "shortNameMapping.json").writeText(shortNameMapping.toJsonString(prettyPrint = true))
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

    File(buildPath, ".nojekyll").createNewFile()
    File(buildPath, "CNAME").writeText("chainid.network")
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

            if (explorer["standard"] != "EIP3091" && explorer["standard"] != "none") {
                throw(ExplorerStandardMustBeEIP3091OrNone())
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

    jsonObject["parent"]?.let {
        if (it !is JsonObject) {
            throw ParentMustBeObject()
        }

        if (!it.keys.containsAll(setOf("chain", "type"))) {
            throw ParentMustHaveChainAndType()
        }

        val extraFields = it.keys - setOf("chain", "type", "bridges")
        if (extraFields.isNotEmpty()) {
            throw ParentHasExtraFields(extraFields)
        }

        val bridges = it["bridges"]
        if (bridges != null && bridges !is List<*>) {
            throw ParentBridgeNoArray()
        }
        (bridges as? JsonArray<*>)?.forEach { bridge ->
            if (bridge !is JsonObject) {
                throw BridgeNoObject()
            }
            if (bridge.keys.size != 1 || bridge.keys.first() != "url") {
                throw BridgeOnlyURL()
            }
        }

        if (!setOf("L2", "shard").contains(it["type"])) {
            throw ParentHasInvalidType(it["type"] as? String)
        }

        if (!File(chainFile.parentFile, it["chain"] as String + ".json").exists()) {
            throw ParentChainDoesNotExist(it["chain"] as String)
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