package com.mbj.doeat.data.remote.network.api.chat_db.repository

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.mbj.doeat.data.remote.model.ChatItem
import com.mbj.doeat.data.remote.network.adapter.ApiResponse
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.chat_db.ChatDBApi
import com.mbj.doeat.util.DateUtils
import com.mbj.doeat.util.UserDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatDBDataSource @Inject constructor(private val defaultDispatcher: CoroutineDispatcher) : ChatDBApi {

    private val database = FirebaseDatabase.getInstance()
    private val groupChatsRef = database.getReference("group_chats")

    override fun enterChatRoom(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        postUserId: String,
        myUserId: String,
        restaurantName: String,
        createdChatRoom: String
    ): Flow<ApiResponse<Unit>> = flow {
        try {
            val chatRoomDB = groupChatsRef.child(postId)
            val dataSnapshot = chatRoomDB.get().await()

            if (dataSnapshot.value != null) {
                val memberRef = chatRoomDB.child("members")
                memberRef.child(myUserId).setValue(true)
                emit(ApiResultSuccess(Unit))
            } else {
                val newChatRoomRef = groupChatsRef.child(postId)
                newChatRoomRef.child("name").setValue(restaurantName)
                newChatRoomRef.child("createdChatRoomDate").setValue(createdChatRoom)
                val membersRef = newChatRoomRef.child("members")
                membersRef.child(myUserId).setValue(true)
                membersRef.child(postUserId).setValue(true)
                emit(ApiResultSuccess(Unit))
            }
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(defaultDispatcher)

    override fun sendMessage(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        message: String,
        myUserId: String,
        sendMessageTime: String
    ): Flow<ApiResponse<Unit>> = flow<ApiResponse<Unit>> {
        try {
            val myUserInfo = UserDataStore.getLoginResponse()
            val chatItem = ChatItem(
                userId = myUserInfo?.userId,
                message = message,
                profileImage = myUserInfo?.userImageUrl,
                nickname = myUserInfo?.userNickname,
                lastSentTime = DateUtils.getCurrentTime()
            )
            chatItem.chatId = groupChatsRef.child(postId).child("messages").push().key
            groupChatsRef.child(postId).child("messages").child(chatItem.chatId!!).setValue(chatItem)
            emit(ApiResultSuccess(Unit))
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(defaultDispatcher)

    override fun addChatDetailEventListener(
        postId: String,
        onChatItemAdded: (ChatItem) -> Unit
    ): ChildEventListener {
        val chatDetailEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatId = snapshot.key
                val messageData = snapshot.getValue(ChatItem::class.java)

                val chatItem = messageData?.copy(chatId = chatId)
                chatItem?.let { onChatItemAdded(it) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        groupChatsRef.child(postId).child("messages").addChildEventListener(chatDetailEventListener)

        return chatDetailEventListener
    }

    override fun removeChatDetailEventListener(
        postId: String,
        chatDetailEventListener: ChildEventListener?
    ) {
        chatDetailEventListener?.let {
            groupChatsRef.child(postId).child("messages").removeEventListener(it)
        }
    }
}