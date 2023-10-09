package com.mbj.doeat.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    @SerialName("items") val items: List<SearchItem>
)

@Serializable
data class SearchItem(
    @SerialName("title") val title: String,
    @SerialName("link") val link: String,
    @SerialName("category") val category: String,
    @SerialName("roadAddress") val roadAddress: String,
    @SerialName("mapx") val mapx: Int,
    @SerialName("mapy") val mapy: Int
)
