package org.ethereum.lists.chains

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import org.kethereum.rpc.HttpEthereumRPC
import java.io.File
import java.math.BigInteger

val all_fields = listOf(
        "name",
        "shortName",
        "chain",
        "network",
        "chainId",
        "networkId",
        "rpc",
        "faucets",
        "infoURL",
        "nativeCurrency"
)

class FileNameMustMatchChainId : Exception("chainId must match the filename")
class ExtensionMustBeJSON : Exception("filename extension must be json")
class ShouldHaveNoExtraFields(fields: Set<String>) : Exception("should have no extra field $fields")
class ShouldHaveNoMissingFields(fields: Set<String>) : Exception("missing field(s) $fields")
class RPCMustBeList : Exception("rpc must be a list")
class RPCMustBeListOfStrings : Exception("rpc must be a list of strings")

fun checkChain(it: File, connectRPC: Boolean) {
    println("processing $it")
    val jsonObject = Klaxon().parseJsonObject(it.reader())
    val chainAsLong = getNumber(jsonObject, "chainId")

    if (chainAsLong != it.nameWithoutExtension.toLongOrNull()) {
        throw(FileNameMustMatchChainId())
    }

    if (it.extension != "json") {
        throw(ExtensionMustBeJSON())
    }

    getNumber(jsonObject, "networkId")

    val extraFields = jsonObject.map.keys.subtract(all_fields)
    if (extraFields.isNotEmpty()) {
        throw ShouldHaveNoExtraFields(extraFields)
    }

    val missingFields = all_fields.subtract(jsonObject.map.keys)
    if (missingFields.isNotEmpty()) {
        throw ShouldHaveNoMissingFields(missingFields)
    }
    if (connectRPC) {
        if (jsonObject["rpc"] is List<*>) {
            (jsonObject["rpc"] as List<*>).forEach {
                if (it !is String) {
                    throw(RPCMustBeListOfStrings())
                } else {
                    println("connecting to $it")
                    val ethereumRPC = HttpEthereumRPC(it)
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
