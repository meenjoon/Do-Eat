package com.mbj.doeat.di

import android.content.Context
import android.content.SharedPreferences
import com.mbj.doeat.data.local.user_pref.UserPreferenceApi
import com.mbj.doeat.data.local.user_pref.repository.UserPreferenceDataSource
import com.mbj.doeat.util.Constants.SHARED_PREFERENCE_KEY
import com.mbj.doeat.util.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            SHARED_PREFERENCE_KEY,
            Context.MODE_PRIVATE
        )
    }

    @Singleton
    @Provides
    fun providePreferenceManager(sharedPreferences: SharedPreferences): PreferenceManager {
        return PreferenceManager(sharedPreferences)
    }

    @Singleton
    @Provides
    fun provideUserPreferenceDataSource(
        preferenceManager: PreferenceManager,
    ): UserPreferenceApi {
        return UserPreferenceDataSource(preferenceManager)
    }
}
