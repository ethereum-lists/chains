package org.ethereum.lists.chains.model

data class Chain(
        val name: String,
        val shortName: String,
        val chain: String,
        val network: String,
        val chainId: Long,
        val networkId: Long,
        val rpc: List<String>,
        val faucets: List<String>,
        val infoURL: String,
)