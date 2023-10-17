package com.mbj.doeat.data.remote.network.api.chat_db.repository

import com.google.firebase.database.ChildEventListener
import com.mbj.doeat.data.remote.model.ChatItem
import com.mbj.doeat.data.remote.network.adapter.ApiResponse
import com.mbj.doeat.data.remote.network.api.chat_db.ChatDBApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatDBRepository @Inject constructor(private val chatDBDataSource: ChatDBDataSource) :
    ChatDBApi {

    override fun enterChatRoom(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        postUserId: String,
        myUserId: String,
        restaurantName: String,
        createdChatRoom: String
    ): Flow<ApiResponse<Unit>> {
        return chatDBDataSource.enterChatRoom(
            onComplete,
            onError,
            postId,
            postUserId,
            myUserId,
            restaurantName,
            createdChatRoom
        )
    }

    override fun sendMessage(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        message: String,
        myUserId: String,
        sendMessageTime: String
    ): Flow<ApiResponse<Unit>> {
        return chatDBDataSource.sendMessage(
            onComplete,
            onError,
            postId,
            message,
            myUserId,
            sendMessageTime
        )
    }

    override fun addChatDetailEventListener(
        postId: String,
        onChatItemAdded: (ChatItem) -> Unit
    ): ChildEventListener {
        return chatDBDataSource.addChatDetailEventListener(
            postId,
            onChatItemAdded
        )
    }

    override fun removeChatDetailEventListener(
        postId: String,
        chatDetailEventListener: ChildEventListener?
    ) {
        return chatDBDataSource.removeChatDetailEventListener(
            postId,
            chatDetailEventListener
        )
    }
}