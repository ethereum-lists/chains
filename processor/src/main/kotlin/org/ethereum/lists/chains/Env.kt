package org.ethereum.lists.chains

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.ethereum.lists.chains.model.Chain

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
        "slip44",
        "ens",
        "icon",
        "explorers",
        "title",
        "network",
        "parent"
)

val moshi: Moshi = Moshi.Builder().build()
val chainAdapter: JsonAdapter<Chain> = moshi.adapter(Chain::class.java)
