package com.mbj.doeat.data.remote.model

data class ChatItem(
    var chatId: String? = null,
    val userId: Long? = null,
    val message: String? = null,
    val profileImage: String? = null,
    val nickname: String? = null,
    val lastSentTime: String? = null
)
