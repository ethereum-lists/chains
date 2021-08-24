package org.ethereum.lists.chains

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.ethereum.lists.chains.model.Chain

val mandatory_fields = listOf(
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
val optionalFields = listOf(
        "slip44",
        "ens",
        "icon",
        "explorers",
        "parent"
)

val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
val chainAdapter: JsonAdapter<Chain> = moshi.adapter(Chain::class.java)
