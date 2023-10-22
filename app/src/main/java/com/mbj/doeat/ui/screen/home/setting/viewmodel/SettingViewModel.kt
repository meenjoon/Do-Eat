package com.mbj.doeat.ui.screen.home.setting.viewmodel

import androidx.lifecycle.ViewModel
import com.mbj.doeat.data.remote.model.LoginResponse
import com.mbj.doeat.util.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingViewModel@Inject constructor() : ViewModel() {

    private val _userInfo = MutableStateFlow<LoginResponse?>(null)
    val userInfo: StateFlow<LoginResponse?> = _userInfo

    init {
        getUserInfo()
    }

    private fun getUserInfo() {
        _userInfo.value = UserDataStore.getLoginResponse()
    }
}
