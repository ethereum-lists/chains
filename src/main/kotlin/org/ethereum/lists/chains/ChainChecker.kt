package org.ethereum.lists.chains

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import org.kethereum.rpc.EthereumRPC
import java.io.File
import java.math.BigInteger

val all_fields = listOf(
        "name",
        "short_name",
        "chain",
        "network",
        "chain_id",
        "network_id",
        "rpc",
        "faucets",
        "info_url"
)

class FileNameMustMatchChainId : Exception("chain_id must match the filename")
class ExtensionMustBeJSON : Exception("filename extension must be json")
class ShouldHaveNoExtraFields(fields: Set<String>) : Exception("should have no extra field $fields")
class ShouldHaveNoMissingFields(fields: Set<String>) : Exception("missing field(s) $fields")
class RPCMustBeList : Exception("rpc must be a list")
class RPCMustBeListOfStrings : Exception("rpc must be a list of strings")

fun checkChain(it: File) {
    println("processing $it")
    val jsonObject = Klaxon().parseJsonObject(it.reader())
    val chainAsLong = getNumber(jsonObject, "chain_id")

    if (chainAsLong != it.nameWithoutExtension.toLongOrNull()) {
        throw(FileNameMustMatchChainId())
    }

    if (it.extension != "json") {
        throw(ExtensionMustBeJSON())
    }

    getNumber(jsonObject, "network_id")

    val extraFields = jsonObject.map.keys.subtract(all_fields)
    if (extraFields.isNotEmpty()) {
        throw ShouldHaveNoExtraFields(extraFields)
    }

    val missingFields = all_fields.subtract(jsonObject.map.keys)
    if (missingFields.isNotEmpty()) {
        throw ShouldHaveNoMissingFields(missingFields)
    }

    if (jsonObject["rpc"] is List<*>) {
        (jsonObject["rpc"] as List<*>).forEach {
            if (it !is String) {
                throw(RPCMustBeListOfStrings())
            } else {
                println("connecting to $it")
                val ethereumRPC = EthereumRPC(it)
                println("Client:" + ethereumRPC.clientVersion()?.result)
                println("BlockNumber:" + ethereumRPC.blockNumber()?.result?.tryBigint())
                println("GasPrice:" + ethereumRPC.gasPrice()?.result?.tryBigint())
            }
        }
        println()
    } else {
        throw(RPCMustBeList())
    }
}


fun String.tryBigint() = if (startsWith("0x")) {
    try {
        BigInteger(removePrefix("0x"), 16)
    } catch (e: NumberFormatException) {
        null
    }
} else {
    null
}
private fun getNumber(jsonObject: JsonObject, field: String): Long {
    return when (val chainId = jsonObject[field]) {
        is Int -> chainId.toLong()
        is Long -> chainId
        else -> throw(Exception("chain_id must be a number"))
    }
}
