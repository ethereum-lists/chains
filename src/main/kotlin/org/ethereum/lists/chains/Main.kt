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

fun main(args: Array<String>) {

    val allFiles = File("_data/chains").listFiles() ?: return
    allFiles.filter { !it.isDirectory }.forEach {
        checkChain(it, args.contains("rpcConnect"))
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
        if (!File(iconsPath,"$it.json").exists()) {
            error("The Icon $it does not exist - was used in ${chainFile.name}")
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