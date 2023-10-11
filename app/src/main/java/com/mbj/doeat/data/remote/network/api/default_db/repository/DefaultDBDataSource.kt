package com.mbj.doeat.data.remote.network.api.default_db.repository

import com.mbj.doeat.data.remote.model.LoginRequest
import com.mbj.doeat.data.remote.model.LoginResponse
import com.mbj.doeat.data.remote.model.Party
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
}
