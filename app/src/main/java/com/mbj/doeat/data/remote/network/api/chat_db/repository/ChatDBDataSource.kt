package com.mbj.doeat.data.remote.network.api.chat_db.repository

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mbj.doeat.data.remote.model.ChatItem
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.data.remote.model.LoginResponse
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
                memberRef.child(myUserId).setValue("guest")
                emit(ApiResultSuccess(Unit))
            } else {
                val newChatRoomRef = groupChatsRef.child(postId)
                newChatRoomRef.child("name").setValue(restaurantName)
                newChatRoomRef.child("createdChatRoomDate").setValue(createdChatRoom)
                newChatRoomRef.child("postId").setValue(postId)
                val membersRef = newChatRoomRef.child("members")
                if (myUserId == postUserId) {
                    membersRef.child(myUserId).setValue("master")
                } else {
                    membersRef.child(myUserId).setValue("guest")
                    membersRef.child(postUserId).setValue("master")
                }
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
            groupChatsRef.child(postId).child("lastMessage").setValue(message)
            emit(ApiResultSuccess(Unit))
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(defaultDispatcher)

    override fun getChatRoomItem(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        onChatRoomItem: (ChatRoom?) -> Unit
    ) {
        try {
            groupChatsRef.child(postId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val chatRoomData = dataSnapshot.getValue(ChatRoom::class.java)
                        val chatRoomItem = chatRoomData?.copy(postId = postId)
                        onChatRoomItem(chatRoomItem)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        onChatRoomItem(null)
                    }
                })
        } catch (e: Exception) {
            onChatRoomItem(null)
        }
    }

    override fun getPeopleInChatRoomListener(
        postId: String,
        onPeopleRetrieved: (LoginResponse?) -> Unit
    ): ChildEventListener {
        val peopleEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val userId = snapshot.key
                val userRef = database.getReference("users")

                if (userId == null) {
                    onPeopleRetrieved(null)
                } else {
                    userRef.child(userId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val userData = snapshot.getValue(LoginResponse::class.java)
                                Log.d("@@ userData", "${userData}")
                                onPeopleRetrieved(userData)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                }
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
        groupChatsRef.child(postId).child("members").addChildEventListener(peopleEventListener)
        return peopleEventListener
    }

    override fun removeGetPeopleInChatRoomListener(
        postId: String,
        getPeopleInChatRoomListener: ChildEventListener?
    ) {
        getPeopleInChatRoomListener?.let {
            groupChatsRef.child(postId).child("members").removeEventListener(it)
        }
    }

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
