package com.mbj.doeat.data.remote.network.api.default_db.repository

import com.mbj.doeat.data.remote.model.FindUserRequest
import com.mbj.doeat.data.remote.model.LoginRequest
import com.mbj.doeat.data.remote.model.LoginResponse
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.PartyPostIdRequestDto
import com.mbj.doeat.data.remote.model.PartyPostRequest
import com.mbj.doeat.data.remote.model.UserIdRequest
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

    override fun getAllPartyList(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<List<Party>>> {
        return defaultDBDataSource.getAllPartyList(
            onComplete = onComplete,
            onError = onError
        )
    }

    override fun deleteParty(
        partyPostIdRequestDto: PartyPostIdRequestDto,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<Unit>> {
        return defaultDBDataSource.deleteParty(
            partyPostIdRequestDto,
            onComplete = onComplete,
            onError = onError
        )
    }

    override fun getAllUserList(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<List<LoginResponse>>> {
        return defaultDBDataSource.getAllUserList(
            onComplete = onComplete,
            onError = onError
        )
    }

    override fun getMyPartyList(
        userIdRequest: UserIdRequest,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<List<Party>>> {
        return defaultDBDataSource.getMyPartyList(
            userIdRequest,
            onComplete,
            onError
        )
    }

    override fun deleteUser(
        userIdRequest: UserIdRequest,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<Unit>> {
        return defaultDBDataSource.deleteUser(
            userIdRequest,
            onComplete,
            onError
        )
    }
}
