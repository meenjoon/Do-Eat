package com.mbj.doeat.data.local.user_pref

interface UserPreferenceApi {

    fun saveAutoLoginState(autoLoginState: Boolean)
    fun getSaveAutoLoginState(): Boolean
}
