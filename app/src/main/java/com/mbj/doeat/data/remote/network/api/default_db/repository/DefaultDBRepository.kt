package com.mbj.doeat.data.remote.network.api.default_db.repository

import com.mbj.doeat.data.remote.model.LoginRequest
import com.mbj.doeat.data.remote.model.LoginResponse
import com.mbj.doeat.data.remote.network.adapter.ApiResponse
import com.mbj.doeat.data.remote.network.api.default_db.DefaultDBApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultDBRepository @Inject constructor(
    private val defaultDBDataSource: DefaultDBDataSource
) : DefaultDBApi {

    override fun signIn(
        loginRequest: LoginRequest,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<LoginResponse>> {
        return defaultDBDataSource.signIn(
            loginRequest,
            onComplete = onComplete,
            onError = onError
        )
    }
}
