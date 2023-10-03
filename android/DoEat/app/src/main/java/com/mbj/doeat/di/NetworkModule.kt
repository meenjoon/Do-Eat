package com.mbj.doeat.di

import com.mbj.doeat.BuildConfig
import com.mbj.doeat.data.remote.network.adapter.ApiCallAdapterFactory
import com.mbj.doeat.data.remote.network.api.FamousRestaurantApi
import com.mbj.doeat.data.remote.network.repository.FamousRestaurantDataSource
import com.mbj.doeat.data.remote.network.service.SearchService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
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
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.NAVER_OEPNAPI_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(ApiCallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideSearchService(retrofit: Retrofit): SearchService {
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
