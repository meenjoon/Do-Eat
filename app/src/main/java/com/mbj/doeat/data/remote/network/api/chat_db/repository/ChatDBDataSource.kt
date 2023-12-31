package com.mbj.doeat.data.remote.network.api.chat_db.repository

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mbj.doeat.data.remote.model.ChatItem
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.data.remote.model.InMember
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
            val memberKey = groupChatsRef.child(postId).child("members").push().key

            val chatRoomDB = groupChatsRef.child(postId)
            val dataSnapshot = chatRoomDB.get().await()

            if (dataSnapshot.value != null) {
                val membersRef = chatRoomDB.child("members")
                if (myUserId == postUserId) {
                    val hostMember = InMember(inMemberId = memberKey, userId = myUserId, guest = false)
                    membersRef.child(memberKey!!).setValue(hostMember)
                } else {
                    val guestMember = InMember(inMemberId = memberKey, userId = myUserId, guest = true)
                    membersRef.child(memberKey!!).setValue(guestMember)
                }
                emit(ApiResultSuccess(Unit))
            } else {
                val newChatRoomRef = groupChatsRef.child(postId)
                newChatRoomRef.child("name").setValue(restaurantName)
                newChatRoomRef.child("createdChatRoomDate").setValue(createdChatRoom)
                newChatRoomRef.child("postId").setValue(postId)
                val membersRef = newChatRoomRef.child("members")
                if (myUserId == postUserId) {
                    val hostMember = InMember(inMemberId = memberKey, userId = myUserId, guest = false)
                    membersRef.child(memberKey!!).setValue(hostMember)
                } else {
                    val guestMember = InMember(inMemberId = memberKey, userId = myUserId, guest = true)
                    val hostMember = InMember(inMemberId = memberKey, userId = postUserId, guest = false)
                    val hostKey = groupChatsRef.child(postId).child("members").push().key
                    membersRef.child(memberKey!!).setValue(guestMember)
                    membersRef.child(hostKey!!).setValue(hostMember)
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
        sendMessageTime: String,
    ): Flow<ApiResponse<Unit>> = flow<ApiResponse<Unit>> {
        try {
            val myUserInfo = UserDataStore.getLoginResponse()
            val chatItem = ChatItem(
                userId = myUserInfo?.userId,
                message = message,
                profileImage = myUserInfo?.userImageUrl,
                nickname = myUserInfo?.userNickname,
                lastSentTime = DateUtils.getCurrentTime(),
            )
            chatItem.chatId = groupChatsRef.child(postId).child("messages").push().key
            val newChatItem = chatItem.copy(chatId = chatItem.chatId)
            groupChatsRef.child(postId).child("messages").child(chatItem.chatId!!).setValue(newChatItem)
            groupChatsRef.child(postId).child("lastMessage").setValue(message)
            groupChatsRef.child(postId).child("lastMessageDate").setValue(DateUtils.getCurrentTime())
            emit(ApiResultSuccess(Unit))
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(defaultDispatcher)

    override fun leaveChatRoom(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        inMemberKey: String,
        chatItemList: List<ChatItem>
    ): Flow<ApiResponse<Unit>> = flow<ApiResponse<Unit>> {
        try {
            val myUserInfo = UserDataStore.getLoginResponse()
            val myUserId = myUserInfo?.userId.toString()
            val chatRoomRef = groupChatsRef.child(postId)
            chatRoomRef.child("members").child(inMemberKey).removeValue()

            chatItemList.forEach { chatItem ->
                if (chatItem.userId.toString() == myUserId) {
                    val messageId = chatItem.chatId
                    messageId?.let { chatRoomRef.child("messages").child(it).removeValue() }
                }
            }
            emit(ApiResultSuccess(Unit))
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(defaultDispatcher)

    override fun deleteChatRoom(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
    ): Flow<ApiResponse<Unit>> = flow<ApiResponse<Unit>> {
        try {
            groupChatsRef.child(postId).removeValue()
            emit(ApiResultSuccess(Unit))
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(defaultDispatcher)

    override fun deleteAllChatRoomsForUserID(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        userIdToDelete: String,
        postIdsToDelete: Set<String>,
        response: (Unit?) -> Unit
    ) {
        try {
            groupChatsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatRooms = mutableListOf<ChatRoom>()

                    for (childSnapshot in snapshot.children) {
                        val chatRoom = childSnapshot.getValue(ChatRoom::class.java)
                        chatRoom?.let { chatRooms.add(it) }
                    }

                    val modifiedChatRooms = chatRooms.map { chatRoom ->
                        val filteredMembers = chatRoom.members.orEmpty().filterValues { inMember ->
                            inMember.userId != userIdToDelete
                        }

                        val filteredMessages = chatRoom.messages.orEmpty().filterValues { chatItem ->
                            chatItem.userId.toString() != userIdToDelete
                        }

                        val modifiedChatRoom = chatRoom.copy(
                            members = filteredMembers,
                            messages = filteredMessages
                        )

                        if (filteredMembers.isEmpty() || filteredMessages.isEmpty()) {
                            null
                        } else {
                            modifiedChatRoom
                        }
                    }.filterNotNull().filter { it.postId !in postIdsToDelete }

                    groupChatsRef.removeValue()

                    for (chatRoom in modifiedChatRooms) {
                        val chatRoomPostId = chatRoom.postId
                        groupChatsRef.child(chatRoomPostId.toString()).setValue(chatRoom).addOnSuccessListener {
                            response(Unit)
                            onComplete()
                        }.addOnCanceledListener {
                            response(null)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    response(null)
                    onError(null)
                }
            })

        } catch (e: Exception) {
            response(null)
            onError(null)
        }
    }

    override fun addChatDetailEventListener(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        onChatItemAdded: (ChatItem) -> Unit
    ): ChildEventListener {
        val chatDetailEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatId = snapshot.key
                val messageData = snapshot.getValue(ChatItem::class.java)

                val chatItem = messageData?.copy(chatId = chatId)
                chatItem?.let { onChatItemAdded(it) }
                onComplete()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
                onError(null)
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

    override fun addChatRoomsAllEventListener(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        onChatRoomItemAdded: (List<ChatRoom>?) -> Unit
    ): ValueEventListener {

        val chatRoomsEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatRoomList = snapshot.children.mapNotNull {
                    it.getValue(ChatRoom::class.java)
                }
                onChatRoomItemAdded(chatRoomList)
                onComplete()
            }

            override fun onCancelled(error: DatabaseError) {
                onError(null)
            }
        }
        groupChatsRef.addValueEventListener(chatRoomsEventListener)

        return chatRoomsEventListener
    }

    override fun removeChatRoomsAllEventListener(chatRoomsAllEventListener: ValueEventListener?) {
        chatRoomsAllEventListener?.let { groupChatsRef.removeEventListener(it) }
    }

    override fun addChatRoomsEventListener(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        onChatRoomItemAdded: (ChatRoom?) -> Unit
    ): ValueEventListener {
        val chatRoomsEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatRoom = snapshot.getValue(ChatRoom::class.java)
                onChatRoomItemAdded(chatRoom)
                onComplete()
            }

            override fun onCancelled(error: DatabaseError) {
                onError(null)
            }
        }
        groupChatsRef.child(postId).addValueEventListener(chatRoomsEventListener)

        return chatRoomsEventListener
    }

    override fun removeChatRoomsEventListener(chatRoomsEventListener: ValueEventListener?) {
        chatRoomsEventListener?.let { groupChatsRef.removeEventListener(it) }
    }
}
