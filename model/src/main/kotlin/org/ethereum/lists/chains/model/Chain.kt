package org.ethereum.lists.chains.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Chain(
    val name: String,
    val shortName: String,
    val chain: String,
    val network: String,
    val chainId: Long,
    val networkId: Long,
    val rpc: List<String>,
    val faucets: List<String>,
    val explorers: List<Explorer>?,
    val infoURL: String,
    val title: String?
)