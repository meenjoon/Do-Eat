package com.mbj.doeat.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FindUserRequest(
    @SerialName("kakaoUserId") val kakaoUserId: Long,
)
