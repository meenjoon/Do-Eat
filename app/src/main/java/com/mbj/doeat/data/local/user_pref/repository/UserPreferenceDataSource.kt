package com.mbj.doeat.data.local.user_pref.repository

import com.mbj.doeat.data.local.user_pref.UserPreferenceApi
import com.mbj.doeat.util.Constants.AUTO_LOGIN
import com.mbj.doeat.util.PreferenceManager
import javax.inject.Inject

class UserPreferenceDataSource @Inject constructor(private val preferenceManager: PreferenceManager) : UserPreferenceApi {

    override fun saveAutoLoginState(autoLoginState: Boolean) {
        preferenceManager.putBoolean(AUTO_LOGIN, autoLoginState)
    }

    override fun getSaveAutoLoginState(): Boolean {
        return preferenceManager.getBoolean(AUTO_LOGIN, false)
    }
}
