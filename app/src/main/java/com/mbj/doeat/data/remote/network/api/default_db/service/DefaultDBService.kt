package com.mbj.doeat.data.remote.network.api.default_db.service

import com.mbj.doeat.data.remote.model.LoginRequest
import com.mbj.doeat.data.remote.model.LoginResponse
import com.mbj.doeat.data.remote.network.adapter.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface DefaultDBService {

    @POST("user")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>
}
