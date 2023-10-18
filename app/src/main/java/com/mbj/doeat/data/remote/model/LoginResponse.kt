package com.mbj.doeat.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("userId") val userId: Long? = 0,
    @SerialName("kakaoUserId") val kakaoUserId: Long? = 0,
    @SerialName("userNickname") val userNickname: String? = "",
    @SerialName("userImageUrl") val userImageUrl: String? = ""
)
