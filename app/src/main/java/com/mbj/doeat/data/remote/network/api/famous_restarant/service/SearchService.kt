package com.mbj.doeat.data.remote.network.api.famous_restarant.service

import com.mbj.doeat.data.remote.model.SearchResult
import com.mbj.doeat.data.remote.network.adapter.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("v1/search/local.json")
    suspend fun getFamousRestaurant(
        @Query("query") query: String,
        @Query("display") display: Int
    ): ApiResponse<SearchResult>
}
