package com.mbj.doeat.util

import com.mbj.doeat.data.remote.model.LoginResponse

object UserDataStore {

    private var loginResponse: LoginResponse? = null

    fun saveLoginResponse(response: LoginResponse) {
        loginResponse = response
    }

    fun getLoginResponse(): LoginResponse? {
        return loginResponse
    }
}
