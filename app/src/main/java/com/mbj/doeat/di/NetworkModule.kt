package com.mbj.doeat.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mbj.doeat.BuildConfig
import com.mbj.doeat.data.remote.network.adapter.ApiCallAdapterFactory
import com.mbj.doeat.data.remote.network.api.FamousRestaurantApi
import com.mbj.doeat.data.remote.network.repository.FamousRestaurantDataSource
import com.mbj.doeat.data.remote.network.service.SearchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class RestaurantListRetrofit

@Qualifier
annotation class RestaurantListOkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    @RestaurantListOkHttpClient
    fun provideOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val header = Interceptor { chain ->
            val originalRequest = chain.request()

            val modifiedRequest = originalRequest.newBuilder()
                .header("X-Naver-Client-Id", BuildConfig.NAVER_OEPNAPI_CLIENT_ID)
                .header("X-Naver-Client-Secret", BuildConfig.NAVER_OEPNAPI_CLIENT_SECRET)
                .build()

            val response = chain.proceed(modifiedRequest)
            response
        }

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(header)
            .build()
    }

    @Singleton
    @Provides
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
        }
    }

    @Singleton
    @Provides
    @RestaurantListRetrofit
    fun provideRestaurantListRetrofit(
        @RestaurantListOkHttpClient okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.NAVER_OEPNAPI_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(ApiCallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideSearchService(@RestaurantListRetrofit retrofit: Retrofit): SearchService {
        return retrofit.create(SearchService::class.java)
    }

    @Singleton
    @Provides
    fun provideFamousRestaurantDataSource(
        apiClient: SearchService,
    ): FamousRestaurantApi {
        return FamousRestaurantDataSource(apiClient)
    }
}
