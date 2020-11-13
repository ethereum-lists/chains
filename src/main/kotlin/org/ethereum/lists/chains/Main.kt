package org.ethereum.lists.chains

import java.io.File
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import org.ethereum.lists.chains.model.*
import org.kethereum.erc55.isValid
import org.kethereum.model.Address
import org.kethereum.rpc.HttpEthereumRPC

fun main(args: Array<String>) {

    File("_data/chains").listFiles()?.forEach {
        checkChain(it, args.contains("rpcConnect"))
    }

}

fun checkChain(it: File, connectRPC: Boolean) {
    println("processing $it")

    parseWithMoshi(it)

    val jsonObject = Klaxon().parseJsonObject(it.reader())
    val chainAsLong = getNumber(jsonObject, "chainId")

    if (chainAsLong != it.nameWithoutExtension.toLongOrNull()) {
        throw(FileNameMustMatchChainId())
    }

    if (it.extension != "json") {
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
    moshi.adapter(Chain::class.java).fromJson(fileToParse.readText())
}

private fun getNumber(jsonObject: JsonObject, field: String): Long {
    return when (val chainId = jsonObject[field]) {
        is Int -> chainId.toLong()
        is Long -> chainId
        else -> throw(Exception("chain_id must be a number"))
    }
}