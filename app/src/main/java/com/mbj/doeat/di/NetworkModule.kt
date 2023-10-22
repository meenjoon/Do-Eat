package com.mbj.doeat.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mbj.doeat.BuildConfig
import com.mbj.doeat.data.remote.network.adapter.ApiCallAdapterFactory
import com.mbj.doeat.data.remote.network.api.chat_db.ChatDBApi
import com.mbj.doeat.data.remote.network.api.chat_db.repository.ChatDBDataSource
import com.mbj.doeat.data.remote.network.api.default_db.DefaultDBApi
import com.mbj.doeat.data.remote.network.api.default_db.repository.DefaultDBDataSource
import com.mbj.doeat.data.remote.network.api.default_db.service.DefaultDBService
import com.mbj.doeat.data.remote.network.api.famous_restarant.FamousRestaurantApi
import com.mbj.doeat.data.remote.network.api.famous_restarant.repository.FamousRestaurantDataSource
import com.mbj.doeat.data.remote.network.api.famous_restarant.service.SearchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
@Qualifier
annotation class DefaultDBRetrofit

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
        defaultDispatcher: CoroutineDispatcher
    ): FamousRestaurantApi {
        return FamousRestaurantDataSource(apiClient, defaultDispatcher)
    }

    @Singleton
    @Provides
    @DefaultDBRetrofit
    fun provideDefaultDBRetrofit(
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.DOEAT_BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(ApiCallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideDefaultDBService(@DefaultDBRetrofit retrofit: Retrofit): DefaultDBService {
        return retrofit.create(DefaultDBService::class.java)
    }

    @Singleton
    @Provides
    fun provideDefaultDBDataSource(
        defaultDBService: DefaultDBService,
        defaultDispatcher: CoroutineDispatcher
    ): DefaultDBApi {
        return DefaultDBDataSource(defaultDBService, defaultDispatcher)
    }

    @Singleton
    @Provides
    fun provideDefaultDispatcher(): CoroutineDispatcher {
        return Dispatchers.Default
    }

    @Singleton
    @Provides
    fun provideChatDBDataSource(defaultDispatcher: CoroutineDispatcher): ChatDBApi {
        return ChatDBDataSource(defaultDispatcher)
    }
}
