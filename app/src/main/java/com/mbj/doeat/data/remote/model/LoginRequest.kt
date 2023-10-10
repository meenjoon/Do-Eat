package com.mbj.doeat.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("kakaoUserId") val kakaoUserId: Long,
    @SerialName("userNickname") val userNickname: String,
    @SerialName("userImageUrl") val userImageUrl: String
)
