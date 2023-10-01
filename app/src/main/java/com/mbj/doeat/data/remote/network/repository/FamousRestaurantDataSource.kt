package com.mbj.doeat.data.remote.network.repository

import android.util.Log
import com.mbj.doeat.data.remote.model.SearchResult
import com.mbj.doeat.data.remote.network.adapter.ApiResponse
import com.mbj.doeat.data.remote.network.adapter.onError
import com.mbj.doeat.data.remote.network.adapter.onException
import com.mbj.doeat.data.remote.network.adapter.onSuccess
import com.mbj.doeat.data.remote.network.api.FamousRestaurantApi
import com.mbj.doeat.data.remote.network.service.SearchService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class FamousRestaurantDataSource @Inject constructor(
    private val searchService: SearchService
) : FamousRestaurantApi {

    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    override fun getSearchResult(
        query: String,
        display: Int,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<ApiResponse<SearchResult>> = flow {
        try {
            val response = searchService.getFamousRestaurant(query = query, display = display)

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
