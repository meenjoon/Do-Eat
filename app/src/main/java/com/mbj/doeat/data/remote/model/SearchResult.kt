package com.mbj.doeat.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResult(
    @Json(name = "items") val items: List<SearchItem>
)

@JsonClass(generateAdapter = true)
data class SearchItem(
    @Json(name = "title") val title: String,
    @Json(name = "link") val link: String,
    @Json(name = "category") val category: String,
    @Json(name = "roadAddress") val roadAddress: String,
    @Json(name = "mapx") val mapx: Int,
    @Json(name = "mapy") val mapy: Int
)
