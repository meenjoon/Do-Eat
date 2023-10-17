package com.mbj.doeat.data.remote.network.api.chat_db

import com.google.firebase.database.ChildEventListener
import com.mbj.doeat.data.remote.model.ChatItem
import com.mbj.doeat.data.remote.network.adapter.ApiResponse
import kotlinx.coroutines.flow.Flow

interface ChatDBApi {

    fun enterChatRoom(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        postUserId: String,
        myUserId: String,
        restaurantName: String,
        createdChatRoom: String,
    ): Flow<ApiResponse<Unit>>

    fun sendMessage(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        message: String,
        myUserId: String,
        sendMessageTime: String
    ): Flow<ApiResponse<Unit>>

    fun addChatDetailEventListener(
        postId: String,
        onChatItemAdded: (ChatItem) -> Unit
    ): ChildEventListener

    fun removeChatDetailEventListener(
        postId: String,
        chatDetailEventListener: ChildEventListener?
    )
}
