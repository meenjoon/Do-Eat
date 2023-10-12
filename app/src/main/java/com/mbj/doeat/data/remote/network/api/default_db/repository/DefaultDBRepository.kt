package com.mbj.doeat.data.remote.network.api.default_db.repository

import com.mbj.doeat.data.remote.model.FindUserRequest
import com.mbj.doeat.data.remote.model.LoginRequest
import com.mbj.doeat.data.remote.model.LoginResponse
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.PartyPostRequest
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

    override fun getPartiesByLocation(
        restaurantLocation: String,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<List<Party>>> {
        return defaultDBDataSource.getPartiesByLocation(
            restaurantLocation,
            onComplete = onComplete,
            onError = onError
        )
    }

    override fun postParty(
        partyPostRequest: PartyPostRequest,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<Party>> {
        return defaultDBDataSource.postParty(
            partyPostRequest,
            onComplete = onComplete,
            onError = onError
        )
    }

    override fun findUser(
        findUserRequest: FindUserRequest,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<LoginResponse>> {
        return defaultDBDataSource.findUser(
            findUserRequest,
            onComplete = onComplete,
            onError = onError
        )
    }
}
