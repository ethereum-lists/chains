package org.ethereum.lists.chains.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Explorer(
    val name: String,
    val url: String,
    val standard: String
)