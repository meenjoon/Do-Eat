package com.mbj.doeat.data.remote.network.api.chat_db

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.ValueEventListener
import com.mbj.doeat.data.remote.model.ChatItem
import com.mbj.doeat.data.remote.model.ChatRoom
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
        sendMessageTime: String
    ): Flow<ApiResponse<Unit>>

    fun getChatRoomItem(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        onChatRoomItem: (ChatRoom?) -> Unit
    )

    fun getAllChatRoomItem(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        onChatRoomItemList: (List<ChatRoom>?) -> Unit
    )

    fun leaveChatRoom(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        inMemberKey: String,
        chatItemList: List<ChatItem>
    ): Flow<ApiResponse<Unit>>

    fun addChatDetailEventListener(
        postId: String,
        onChatItemAdded: (ChatItem) -> Unit
    ): ChildEventListener

    fun removeChatDetailEventListener(
        postId: String,
        chatDetailEventListener: ChildEventListener?
    )

    fun addChatRoomsAllEventListener(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        onChatRoomItemAdded: (List<ChatRoom>?) -> Unit): ValueEventListener

    fun removeChatRoomsAllEventListener(
        chatRoomsAllEventListener: ValueEventListener?
    )

    fun addChatRoomsEventListener(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        onChatRoomItemAdded: (ChatRoom?) -> Unit): ValueEventListener

    fun removeChatRoomsEventListener(
        chatRoomsEventListener: ValueEventListener?
    )
}
