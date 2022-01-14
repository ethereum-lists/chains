package org.ethereum.lists.chains.model

import com.squareup.moshi.JsonClass

enum class ExplorerStandard {
    EIP3091,
    none
}

@JsonClass(generateAdapter = true)
data class Explorer(
    val name: String,
    val url: String,
    val standard: ExplorerStandard
)