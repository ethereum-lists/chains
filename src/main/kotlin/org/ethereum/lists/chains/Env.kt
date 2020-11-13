package org.ethereum.lists.chains

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

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
        "ens"
)

val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
