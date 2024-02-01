package org.ethereum.lists.chains.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NativeCurrency(
    val name : String,
    val symbol : String,
    val decimals : Int,
)