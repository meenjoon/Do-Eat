package com.mbj.doeat.data.remote.network.api.chat_db.repository

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.ValueEventListener
import com.mbj.doeat.data.remote.model.ChatItem
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.data.remote.network.adapter.ApiResponse
import com.mbj.doeat.data.remote.network.api.chat_db.ChatDBApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatDBRepository @Inject constructor(private val chatDBDataSource: ChatDBDataSource) : ChatDBApi {

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
        sendMessageTime: String,
    ): Flow<ApiResponse<Unit>> {
        return chatDBDataSource.sendMessage(
            onComplete,
            onError,
            postId,
            message,
            sendMessageTime,
        )
    }

    override fun leaveChatRoom(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        inMemberKey: String,
        chatItemList: List<ChatItem>
    ): Flow<ApiResponse<Unit>> {
        return chatDBDataSource.leaveChatRoom(
            onComplete,
            onError,
            postId,
            inMemberKey,
            chatItemList
        )
    }

    override fun deleteChatRoom(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String
    ): Flow<ApiResponse<Unit>> {
        return chatDBDataSource.deleteChatRoom(
            onComplete,
            onError,
            postId
        )
    }

    override fun deleteAllChatRoomsForUserID(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        userIdToDelete: String,
        postIdsToDelete: Set<String>,
        response: (Unit?) -> Unit
    ) {
        return chatDBDataSource.deleteAllChatRoomsForUserID(
            onComplete,
            onError,
            userIdToDelete,
            postIdsToDelete,
            response
        )
    }

    override fun addChatDetailEventListener(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        onChatItemAdded: (ChatItem) -> Unit
    ): ChildEventListener {
        return chatDBDataSource.addChatDetailEventListener(
            onComplete,
            onError,
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

    override fun addChatRoomsAllEventListener(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        onChatRoomItemAdded: (List<ChatRoom>?) -> Unit
    ): ValueEventListener {
        return chatDBDataSource.addChatRoomsAllEventListener(
            onComplete,
            onError,
            onChatRoomItemAdded
        )
    }

    override fun removeChatRoomsAllEventListener(chatRoomsAllEventListener: ValueEventListener?) {
        return chatDBDataSource.removeChatRoomsAllEventListener(chatRoomsAllEventListener)
    }

    override fun addChatRoomsEventListener(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        onChatRoomItemAdded: (ChatRoom?) -> Unit
    ): ValueEventListener {
        return chatDBDataSource.addChatRoomsEventListener(
            onComplete,
            onError,
            postId,
            onChatRoomItemAdded
        )
    }

    override fun removeChatRoomsEventListener(chatRoomsEventListener: ValueEventListener?) {
        return chatDBDataSource.removeChatRoomsEventListener(chatRoomsEventListener)
    }
}
