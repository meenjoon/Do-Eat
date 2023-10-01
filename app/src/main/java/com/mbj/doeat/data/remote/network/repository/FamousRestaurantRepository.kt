package com.mbj.doeat.data.remote.network.repository

import com.mbj.doeat.data.remote.model.SearchResult
import com.mbj.doeat.data.remote.network.adapter.ApiResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FamousRestaurantRepository @Inject constructor(
    private val famousRestaurantDataSource: FamousRestaurantDataSource
) {

    fun getSearchResult(
        query: String,
        display: Int = 5,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<SearchResult>> {
        return famousRestaurantDataSource.getSearchResult(
            query = "$query 맛집",
            display = display,
            onComplete = onComplete,
            onError = onError
        )
    }
}
