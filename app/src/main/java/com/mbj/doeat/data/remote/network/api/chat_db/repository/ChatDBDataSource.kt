package com.mbj.doeat.data.remote.network.api.chat_db.repository

import com.google.firebase.database.FirebaseDatabase
import com.mbj.doeat.data.remote.network.adapter.ApiResponse
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.chat_db.ChatDBApi
import com.mbj.doeat.util.DateUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatDBDataSource @Inject constructor(
    private val defaultDispatcher: CoroutineDispatcher
) : ChatDBApi {

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
                newChatRoomRef.child("createdChatRoomDate").setValue(DateUtils.getCurrentTime())
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
}
