package com.mbj.doeat.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PartyPostRequest(
    @SerialName("userId")
    val userId: Long,
    @SerialName("restaurantName")
    val restaurantName: String,
    @SerialName("category")
    val category: String,
    @SerialName("restaurantLocation")
    val restaurantLocation: String,
    @SerialName("recruitmentLimit")
    val recruitmentLimit: Int,
    @SerialName("currentNumberPeople")
    val currentNumberPeople: Int,
    @SerialName("detail")
    val detail: String,
    @SerialName("link")
    val link: String
)
