package com.mbj.doeat.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatItem(
    @SerialName("chatId") var chatId: String? = null,
    @SerialName("lastSentTime") val lastSentTime: String? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("nickname") val nickname: String? = null,
    @SerialName("profileImage") val profileImage: String? = null,
    @SerialName("userId") val userId: Long? = null
)
