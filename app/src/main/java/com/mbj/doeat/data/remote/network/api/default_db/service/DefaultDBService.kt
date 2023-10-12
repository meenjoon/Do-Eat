package com.mbj.doeat.data.remote.network.api.default_db.service

import com.mbj.doeat.data.remote.model.LoginRequest
import com.mbj.doeat.data.remote.model.LoginResponse
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.PartyPostRequest
import com.mbj.doeat.data.remote.network.adapter.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DefaultDBService {

    @POST("user")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @GET("party/restaurant/{restaurantLocation}")
    suspend fun getPartiesByLocation(@Path("restaurantLocation") restaurantLocation: String): ApiResponse<List<Party>>

    @POST("party/post")
    suspend fun postParty(@Body request: PartyPostRequest): ApiResponse<Party>
}
