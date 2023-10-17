package com.mbj.doeat.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRoom(
    @SerialName("postId") val postId: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("createdChatRoomDate") val createdChatRoomDate: String? = null,
    @SerialName("members") val members: Map<String, String>? = null,
    @SerialName("lastMessage") val lastMessage: String? = null,
)
