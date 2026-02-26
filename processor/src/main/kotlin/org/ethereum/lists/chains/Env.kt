package org.ethereum.lists.chains

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.ipfs.kotlin.IPFS
import io.ipfs.kotlin.IPFSConfiguration
import okhttp3.OkHttpClient
import org.ethereum.lists.chains.model.Chain
import java.time.Duration

val mandatory_fields = listOf(
    "name",
    "shortName",
    "chain",
    "chainId",
    "networkId",
    "rpc",
    "faucets",
    "infoURL",
    "nativeCurrency"
)
val optionalFields = listOf(
    "features",
    "slip44",
    "ens",
    "icon",
    "explorers",
    "title",
    "parent",
    "status",
    "redFlags"
)

val allowedRedFlags = listOf("reusedChainId")

val moshi: Moshi = Moshi.Builder().build()
val chainAdapter: JsonAdapter<Chain> = moshi.adapter(Chain::class.java)

val ipfs by lazy {
    IPFS(
        IPFSConfiguration(
            "http://127.0.0.1:5001/api/v0/",
            OkHttpClient.Builder().readTimeout(Duration.ofMinutes(1)).build(),
            Moshi.Builder().build()
        )
    )
}

val httpPrefixes = listOf("https://", "http://")
val rpcPrefixes = httpPrefixes + listOf("wss://", "ws://")