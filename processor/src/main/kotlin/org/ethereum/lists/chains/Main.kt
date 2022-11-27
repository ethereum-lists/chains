package org.ethereum.lists.chains

import com.beust.klaxon.JsonArray
import java.io.File
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import org.ethereum.lists.chains.model.*
import org.kethereum.erc55.isValid
import org.kethereum.model.Address
import org.kethereum.rpc.HttpEthereumRPC
import java.math.BigInteger
import javax.imageio.ImageIO
import kotlin.io.OnErrorAction.*

val parsedShortNames = mutableSetOf<String>()
val parsedNames = mutableSetOf<String>()

val basePath = File("..")
val dataPath = File(basePath, "_data")
val iconsPath = File(dataPath, "icons")
val iconsDownloadPath = File(dataPath, "iconsDownload")

val chainsPath = File(dataPath, "chains")
private val allFiles = chainsPath.listFiles() ?: error("${chainsPath.absolutePath} must contain the chain json files - but it does not")
private val allChainFiles = allFiles.filter { !it.isDirectory }

private val allIconFilesList = iconsPath.listFiles() ?: error("${iconsPath.absolutePath} must contain the icon json files - but it does not")
private val allIconFiles = allIconFilesList.filter { !it.isDirectory }

fun main(args: Array<String>) {

    doChecks(
        doRPCConnect = args.contains("rpcConnect"),
        doIconDownload = args.contains("iconDownload"),
        verbose = args.contains("verbose")
    )
    createOutputFiles()
}

private fun createOutputFiles() {
    val buildPath = File(basePath, "output").apply { mkdir() }

    val chainJSONArray = JsonArray<JsonObject>()
    val miniChainJSONArray = JsonArray<JsonObject>()

    val chainIconJSONArray = JsonArray<JsonObject>()

    val shortNameMapping = JsonObject()

    // copy raw data so e.g. icons are available - SKIP errors
    File(basePath, "_data").copyRecursively(buildPath, onError = { _, _ -> SKIP })
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

    allIconFiles
        .forEach { iconLocation ->

            val jsonData = Klaxon().parseJsonArray(iconLocation.reader())
            val iconName = iconLocation.toString().replace("../_data/icons/", "").replace(".json", "")

            val iconJson = JsonObject()
            iconJson["name"] = iconName
            iconJson["icons"] = jsonData

            chainIconJSONArray.add(iconJson)
        }

    File(buildPath, "chains.json").writeText(chainJSONArray.toJsonString())

    File(buildPath, "chains.json").writeText(chainJSONArray.toJsonString())
    File(buildPath, "chains_pretty.json").writeText(chainJSONArray.toJsonString(prettyPrint = true))

    File(buildPath, "chains_mini.json").writeText(miniChainJSONArray.toJsonString())
    File(buildPath, "chains_mini_pretty.json").writeText(miniChainJSONArray.toJsonString(prettyPrint = true))

    File(buildPath, "chain_icons_mini.json").writeText(chainIconJSONArray.toJsonString())
    File(buildPath, "chain_icons.json").writeText(chainIconJSONArray.toJsonString(prettyPrint = true))

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

private fun doChecks(doRPCConnect: Boolean, doIconDownload: Boolean, verbose: Boolean) {
    allChainFiles.forEach { file ->
        try {
            checkChain(file, doRPCConnect, verbose)
        } catch (exception: Exception) {
            println("Problem with $file")
            throw exception
        }
    }

    val allIcons = iconsPath.listFiles() ?: return
    val allIconCIDs = mutableSetOf<String>()
    allIcons.forEach {
        checkIcon(it, doIconDownload, allIconCIDs, verbose)
    }

    iconsDownloadPath.listFiles().forEach {
        if (!allIconCIDs.contains(it.name)) throw UnreferencedIcon(it.name, iconsDownloadPath)
    }

    allFiles.filter { it.isDirectory }.forEach { _ ->
        error("should not contain a directory")
    }
}

fun checkIcon(icon: File, withIconDownload: Boolean, allIconCIDs: MutableSet<String>, verbose: Boolean) {
    val obj: JsonArray<*> = Klaxon().parseJsonArray(icon.reader())
    if (verbose) {
        println("checking Icon " + icon.name)
        println("found variants " + obj.size)
    }
    obj.forEach { it ->
        if (it !is JsonObject) {
            error("Icon variant must be an object")
        }

        val url = it["url"] ?: error("Icon must have a URL")

        if (url !is String || !url.startsWith("ipfs://")) {
            error("url must start with ipfs://")
        }

        allIconCIDs.add(url.removePrefix("ipfs://"))

        val iconCID = url.removePrefix("ipfs://")
        val iconDownloadFile = File(iconsDownloadPath, iconCID)

        if (!iconDownloadFile.exists() && withIconDownload) {

            try {

                println("fetching Icon from IPFS $iconCID")

                val iconBytes = ipfs.get.catBytes(iconCID)
                println("Icon size" + iconBytes.size)


                iconDownloadFile.createNewFile()
                iconDownloadFile.writeBytes(iconBytes)
            } catch (e: Exception) {
                println("could not fetch icon from IPFS")
            }
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

        if (iconDownloadFile.exists()) {
            try {
                val imageInputStream = ImageIO.createImageInputStream(iconDownloadFile)
                val imageReader = ImageIO.getImageReaders(imageInputStream).next()
                val image = ImageIO.read(imageInputStream)

                if (imageReader.formatName != format) {
                    error("format in json ($icon) is $format but actually is in imageDownload ${imageReader.formatName}")
                }
                if (image.width != width) {
                    error("width in json ($icon) is $width but actually is in imageDownload ${image.width}")
                }

                if (image.raster.height != height) {
                    error("width in json ($icon) is $height but actually is in imageDownload ${image.height}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                error("problem with image $iconDownloadFile")
            }
        }
    }
}

fun checkChain(chainFile: File, connectRPC: Boolean, verbose: Boolean = false) {
    if (verbose) {
        println("processing $chainFile")
    }

    val jsonObject = Klaxon().parseJsonObject(chainFile.reader())
    val chainIdAsLong = getNumber(jsonObject, "chainId")

    if (chainFile.nameWithoutExtension.startsWith("eip155-")) {
        if (chainIdAsLong.toString() != chainFile.nameWithoutExtension.replace("eip155-", "")) {
            throw (FileNameMustMatchChainId())
        }
    } else {
        throw (UnsupportedNamespace())
    }

    if (chainFile.extension != "json") {
        throw (ExtensionMustBeJSON())
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

    val nameRegex = Regex("^[a-zA-Z0-9\\-\\.\\(\\) ]+$")
    jsonObject["nativeCurrency"]?.let {
        if (it !is JsonObject) {
            throw NativeCurrencyMustBeObject()
        }
        val symbol = it["symbol"]
        if (symbol !is String) {
            throw NativeCurrencySymbolMustBeString()
        }

        if (symbol.length >= 7) {
            throw NativeCurrencySymbolMustHaveLessThan7Chars()
        }
        if (it.keys != setOf("symbol", "decimals", "name")) {
            throw NativeCurrencyCanOnlyHaveSymbolNameAndDecimals()
        }
        if (it["decimals"] !is Int) {
            throw NativeCurrencyDecimalMustBeInt()
        }
        val currencyName = it["name"]
        if (currencyName !is String) {
            throw NativeCurrencyNameMustBeString()
        }

        if (!nameRegex.matches(currencyName)) {
            throw IllegalName("currencyName", currencyName)
        }
    }

    val chainName = jsonObject["name"]
    if (chainName !is String) {
        throw ChainNameMustBeString()
    }

    if (!nameRegex.matches(chainName)) {
        throw IllegalName("chain name", chainName)
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
                throw (ExplorerMustHaveName())
            }

            val url = explorer["url"]
            if (url == null || url !is String || httpPrefixes.none { prefix -> url.startsWith(prefix) } ) {
                throw (ExplorerMustWithHttpsOrHttp())
            }

            if (url.endsWith("/")) {
                throw (ExplorerCannotEndInSlash())
            }

            if (explorer["standard"] != "EIP3091" && explorer["standard"] != "none") {
                throw (ExplorerStandardMustBeEIP3091OrNone())
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
    jsonObject["status"]?.let {
        if (it !is String) {
            throw StatusMustBeString()
        }
        if (!setOf("incubating", "active", "deprecated").contains(it)) {
            throw StatusMustBeIncubatingActiveOrDeprecated()
        }
    }

    jsonObject["redFlags"]?.let { redFlags ->
        if (redFlags !is List<*>) {
            throw RedFlagsMustBeArray()
        }
        redFlags.forEach {
            if (it !is String) {
                throw RedFlagMustBeString()
            }

            if (!allowedRedFlags.contains(it))
                throw (InvalidRedFlags(it))
        }
    }

    jsonObject["parent"]?.let {
        if (it !is JsonObject) {
            throw ParentMustBeObject()
        }

        if (!it.keys.containsAll(setOf("chain", "type"))) {
            throw ParentMustHaveChainAndType()
        }

        val extraParentFields = it.keys - setOf("chain", "type", "bridges")
        if (extraParentFields.isNotEmpty()) {
            throw ParentHasExtraFields(extraParentFields)
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

    parseWithMoshi(chainFile)

    if (jsonObject["rpc"] !is List<*>) {
        throw (RPCMustBeList())
    } else {
        (jsonObject["rpc"] as List<*>).forEach {
            if (it !is String) {
                throw (RPCMustBeListOfStrings())
            } else if (it.isBlank()) {
                throw (RPCCannotBeEmpty())
            } else if (rpcPrefixes.none { prefix -> it.startsWith(prefix) }) {
                throw (InvalidRPCPrefix(it))
            } else {
                if (connectRPC) {
                    var chainId: BigInteger? = null
                    try {
                        println("connecting to $it")
                        val ethereumRPC = HttpEthereumRPC(it)

                        println("Client:" + ethereumRPC.clientVersion())
                        println("BlockNumber:" + ethereumRPC.blockNumber())
                        println("GasPrice:" + ethereumRPC.gasPrice())

                        chainId = ethereumRPC.chainId()?.value
                    } catch (e: Exception) {

                    }
                    chainId?.let { chainId ->
                        if (chainIdAsLong != chainId.toLong()) {
                            error("RPC chainId (${chainId.toLong()}) does not match chainId from json ($chainIdAsLong)")
                        }
                    }
                }
            }
        }
    }
}

fun String.normalizeName() = replace(" ", "").uppercase()

/*
moshi fails for extra commas
https://github.com/ethereum-lists/chains/issues/126
*/
private fun parseWithMoshi(fileToParse: File) {
    val parsedChain = chainAdapter.fromJson(fileToParse.readText())
    val parsedChainNormalizedName = parsedChain!!.name.normalizeName()
    if (parsedNames.contains(parsedChainNormalizedName)) {
        throw NameMustBeUnique(parsedChainNormalizedName)
    }
    parsedNames.add(parsedChainNormalizedName)

    val parsedChainNormalizedShortName = parsedChain.shortName.normalizeName()
    if (parsedShortNames.contains(parsedChainNormalizedShortName)) {
        throw ShortNameMustBeUnique(parsedChainNormalizedShortName)
    }

    if (parsedChainNormalizedShortName == "*") {
        throw ShortNameMustNotBeStar()
    }

    parsedShortNames.add(parsedChainNormalizedShortName)
}

private fun getNumber(jsonObject: JsonObject, field: String): Long {
    return when (val chainId = jsonObject[field]) {
        is Int -> chainId.toLong()
        is Long -> chainId
        else -> throw (Exception("not a number at $field"))
    }
}