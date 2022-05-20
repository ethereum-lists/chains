package org.ethereum.lists.chains.model

import com.squareup.moshi.JsonClass
import org.ethereum.lists.chains.model.ExplorerStandard.*

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

fun List<Explorer>.filterEIP3019() = filter { it.standard == EIP3091 }