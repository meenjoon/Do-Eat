package com.mbj.doeat.data.local.user_pref.repository

import com.mbj.doeat.data.local.user_pref.UserPreferenceApi
import javax.inject.Inject

class UserPreferenceRepository @Inject constructor(private val preferenceDataSource: UserPreferenceDataSource) : UserPreferenceApi {

    override fun saveAutoLoginState(autoLoginState: Boolean) {
        preferenceDataSource.saveAutoLoginState(autoLoginState)
    }

    override fun getSaveAutoLoginState(): Boolean {
        return preferenceDataSource.getSaveAutoLoginState()
    }
}
