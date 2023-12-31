package com.mbj.doeat.data.remote.network.api.default_db.repository

import com.mbj.doeat.data.remote.model.FindUserRequest
import com.mbj.doeat.data.remote.model.LoginRequest
import com.mbj.doeat.data.remote.model.LoginResponse
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.PartyPostIdRequestDto
import com.mbj.doeat.data.remote.model.PartyPostRequest
import com.mbj.doeat.data.remote.model.UserIdRequest
import com.mbj.doeat.data.remote.network.adapter.ApiResponse
import com.mbj.doeat.data.remote.network.adapter.onError
import com.mbj.doeat.data.remote.network.adapter.onException
import com.mbj.doeat.data.remote.network.adapter.onSuccess
import com.mbj.doeat.data.remote.network.api.default_db.DefaultDBApi
import com.mbj.doeat.data.remote.network.api.default_db.service.DefaultDBService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class DefaultDBDataSource @Inject constructor(
    private val defaultDBService: DefaultDBService,
    private val defaultDispatcher: CoroutineDispatcher
) : DefaultDBApi {

    override fun signIn(
        loginRequest: LoginRequest,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<LoginResponse>> = flow {
        try {
            val response = defaultDBService.login(loginRequest)

            response.onSuccess {
                emit(response)
            }.onError { code, message ->
                onError("code: $code, message: $message")
            }.onException {
                onError(it.message)
            }
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(defaultDispatcher)

    override fun getPartiesByLocation(
        restaurantLocation: String,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<List<Party>>> = flow {
        try {
            val response = defaultDBService.getPartiesByLocation(restaurantLocation)
            response.onSuccess {
                emit(response)
            }.onError { code, message ->
                onError("code: $code, message: $message")
            }.onException {
                onError(it.message)
            }
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(defaultDispatcher)

    override fun postParty(
        partyPostRequest: PartyPostRequest,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<Party>> = flow {
        try {
            val response = defaultDBService.postParty(partyPostRequest)

            response.onSuccess {
                emit(response)
            }.onError { code, message ->
                onError("code: $code, message: $message")
            }.onException {
                onError(it.message)
            }
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(defaultDispatcher)

    override fun findUser(
        findUserRequest: FindUserRequest,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<LoginResponse>> = flow {
        try {
            val response = defaultDBService.findUser(findUserRequest)

            response.onSuccess {
                emit(response)
            }.onError { code, message ->
                onError("code: $code, message: $message")
            }.onException {
                onError(it.message)
            }
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(defaultDispatcher)

    override fun getAllPartyList(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<List<Party>>> = flow {
        try {
            val response = defaultDBService.getAllPartyList()
            response.onSuccess {
                emit(response)
            }.onError { code, message ->
                onError("code: $code, message: $message")
            }.onException {
                onError(it.message)
            }
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(defaultDispatcher)

    override fun deleteParty(
        partyPostIdRequestDto: PartyPostIdRequestDto,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<Unit>> = flow {
        try {
            val response = defaultDBService.partyDelete(partyPostIdRequestDto)
            response.onSuccess {
                emit(response)
            }.onError { code, message ->
                onError("code: $code, message: $message")
            }.onException {
                onError(it.message)
            }
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(defaultDispatcher)

    override fun getAllUserList(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<List<LoginResponse>>> = flow {
        try {
            val response = defaultDBService.getAllUserList()
            response.onSuccess {
                emit(response)
            }.onError { code, message ->
                onError("code: $code, message: $message")
            }.onException {
                onError(it.message)
            }
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(defaultDispatcher)

    override fun getMyPartyList(
        userIdRequest: UserIdRequest,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<List<Party>>> = flow {
        try {
            val response = defaultDBService.getMyPartyList(userIdRequest)
            response.onSuccess {
                emit(response)
            }.onError { code, message ->
                onError("code: $code, message: $message")
            }.onException {
                onError(it.message)
            }
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(defaultDispatcher)

    override fun deleteUser(
        userIdRequest: UserIdRequest,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<Unit>> = flow {
        try {
            val response = defaultDBService.deleteUser(userIdRequest)
            response.onSuccess {
                emit(response)
            }.onError { code, message ->
                onError("code: $code, message: $message")
            }.onException {
                onError(it.message)
            }
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(defaultDispatcher)
}
