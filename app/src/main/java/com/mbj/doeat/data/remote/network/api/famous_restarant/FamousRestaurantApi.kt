package com.mbj.doeat.data.remote.network.api.famous_restarant

import com.mbj.doeat.data.remote.model.SearchResult
import com.mbj.doeat.data.remote.network.adapter.ApiResponse
import kotlinx.coroutines.flow.Flow

interface FamousRestaurantApi {

    fun getSearchResult(
        query: String,
        display: Int = 5,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<SearchResult>>
}
