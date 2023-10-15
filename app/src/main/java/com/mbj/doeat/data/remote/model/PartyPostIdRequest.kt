package com.mbj.doeat.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PartyPostIdRequestDto(
    @SerialName("postId") val postId: Long,
)
