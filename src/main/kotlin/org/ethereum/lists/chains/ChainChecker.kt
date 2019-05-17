package org.ethereum.lists.chains

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import java.io.File

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

class FileNameMustMatchChainId: Exception("chain_id must match the filename")
class ShouldHaveNoExtraFields(fields: Set<String>): Exception("should have no extra field $fields")
class ShouldHaveNoMissingFields(fields: Set<String>): Exception("missing field(s) $fields")

fun checkChain(it: File) {
    println("processing $it")
    val jsonObject = Klaxon().parseJsonObject(it.reader())
    val chainAsLong = getNumber(jsonObject, "chain_id")

    if (chainAsLong != it.nameWithoutExtension.toLongOrNull()) {
        throw(FileNameMustMatchChainId())
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
}


private fun getNumber(jsonObject: JsonObject, field: String): Long {
    return when (val chainId = jsonObject[field]) {
        is Int -> chainId.toLong()
        is Long -> chainId
        else -> throw(Exception("chain_id must be a number"))
    }
}

/*

open class InvalidTokenException(message: String) : IllegalArgumentException(message)
class InvalidChecksum(message: String) : InvalidTokenException("The address is not valid with ERC-55 checksum $message")

class InvalidAddress(address: Address) : InvalidTokenException("The address is not valid $address")
class InvalidDecimals : InvalidTokenException("Decimals must be a number")
class InvalidFileName : InvalidTokenException("Filename must be the address + .json")
class InvalidWebsite : InvalidTokenException("Website invalid")
class InvalidJSON(message: String?) : InvalidTokenException("JSON invalid $message")
class InvalidDeprecationMigrationType : InvalidTokenException("Invalid Deprecation Migration type - currently only auto and instructions: is allowed")
class InvalidDeprecationTime : InvalidTokenException("Invalid Deprecation Time - Must be ISO8601")

fun String.tryBigint() = if (startsWith("0x")) {
    try {
        BigInteger(removePrefix("0x"), 16)
    } catch (e: NumberFormatException) {
        null
    }
} else {
    null
}

fun checkChainsFile(file: File) {
    val moshi = Moshi.Builder().build()


    val listMyData = Types.newParameterizedType(List::class.java, Chain::class.java)
    val foo: JsonAdapter<List<Chain>> = moshi.adapter(listMyData)

    foo.fromJson(Okio.buffer(Okio.source(file)))?.forEach {
        println()
        println(it.name)
        for (s in it.rpc) {
            val ethereumRPC = EthereumRPC(s)
            println("Client:" + ethereumRPC.clientVersion()?.result)
            println("BlockNumber:" + ethereumRPC.blockNumber()?.result?.tryBigint())
            println("GasPrice:" + ethereumRPC.gasPrice()?.result?.tryBigint())
        }

    }
}

 */