package com.mbj.doeat.data.remote.network.api.default_db

import com.mbj.doeat.data.remote.model.FindUserRequest
import com.mbj.doeat.data.remote.model.LoginRequest
import com.mbj.doeat.data.remote.model.LoginResponse
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.PartyPostRequest
import com.mbj.doeat.data.remote.network.adapter.ApiResponse
import kotlinx.coroutines.flow.Flow

interface DefaultDBApi {

    fun signIn(
        loginRequest: LoginRequest,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<LoginResponse>>

    fun getPartiesByLocation(
        restaurantLocation: String,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<List<Party>>>

    fun postParty(
        partyPostRequest: PartyPostRequest,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<Party>>

    fun findUser(
        findUserRequest: FindUserRequest,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<LoginResponse>>
}
