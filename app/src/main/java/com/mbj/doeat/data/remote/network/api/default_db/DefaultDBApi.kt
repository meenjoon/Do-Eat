package com.mbj.doeat.data.remote.network.api.default_db

import com.mbj.doeat.data.remote.model.LoginRequest
import com.mbj.doeat.data.remote.model.LoginResponse
import com.mbj.doeat.data.remote.network.adapter.ApiResponse
import kotlinx.coroutines.flow.Flow

interface DefaultDBApi {

    fun signIn(
        loginRequest: LoginRequest,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<LoginResponse>>
}
