package com.mbj.doeat.ui.screen.signin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.mbj.doeat.data.local.user_pref.repository.UserPreferenceRepository
import com.mbj.doeat.data.remote.model.LoginRequest
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.default_db.repository.DefaultDBRepository
import com.mbj.doeat.ui.graph.Graph
import com.mbj.doeat.util.NavigationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.kakao.sdk.user.UserApiClient
import com.mbj.doeat.data.remote.model.FindUserRequest
import com.mbj.doeat.util.UserDataStore.saveLoginResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val defaultDBRepository: DefaultDBRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
) : ViewModel() {

    private val _isAutoLoginState = MutableStateFlow<Boolean>(userPreferenceRepository.getSaveAutoLoginState())
    val isAutoLoginState: StateFlow<Boolean> = _isAutoLoginState

    private val _isLoadingView = MutableStateFlow<Boolean>(false)
    val isLoadingView: StateFlow<Boolean> = _isLoadingView

    private val _isNetworkError = MutableSharedFlow<Boolean>()
    val isNetworkError: SharedFlow<Boolean> = _isNetworkError.asSharedFlow()

    private val _showNetworkError = MutableStateFlow<Boolean>(false)
    val showNetworkError: StateFlow<Boolean> = _showNetworkError

    fun signIn(
        loginRequest: LoginRequest,
        navHostController: NavHostController
    ) {
        viewModelScope.launch {
            defaultDBRepository.signIn(loginRequest,
                onComplete = {
                },
                onError = {
                    toggleNetworkErrorToggle()
                }
            ).collectLatest { loginResponse ->
                if (loginResponse is ApiResultSuccess) {
                    saveLoginResponse(loginResponse.data)
                    NavigationUtils.navigate(
                        controller = navHostController,
                        routeName = Graph.HOME,
                        backStackRouteName = Graph.AUTHENTICATION
                    )
                }
            }
        }
    }

    fun checkAccessTokenAndNavigate(navHostController: NavHostController) {
        viewModelScope.launch {
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                setLoadingState(true)
                if (tokenInfo != null) {
                    val isAutoLogin = userPreferenceRepository.getSaveAutoLoginState()
                    if (isAutoLogin) {
                        findUserAndNavigateHome(tokenInfo.id!!, navHostController)
                    } else {
                        setLoadingState(false)
                    }
                } else {
                    setLoadingState(false)
                }
            }
        }
    }

    private fun findUserAndNavigateHome(kakaoUserId: Long, navHostController: NavHostController) {
        viewModelScope.launch {
            defaultDBRepository.findUser(
                findUserRequest = FindUserRequest(kakaoUserId),
                onComplete = {
                    toggleNetworkErrorToggle()
                },
                onError = {
                }
            ).collectLatest { findUserResponse ->
                if (findUserResponse is ApiResultSuccess) {
                    saveLoginResponse(findUserResponse.data)
                    NavigationUtils.navigate(
                        controller = navHostController,
                        routeName = Graph.HOME,
                        backStackRouteName = Graph.AUTHENTICATION
                    )
                }
            }
        }
    }

    private fun toggleNetworkErrorToggle() {
        viewModelScope.launch {
            _isNetworkError.emit(true)
            _showNetworkError.value = !showNetworkError.value
        }
    }

    fun setAutoLoginEnabled(isChecked: Boolean) {
        userPreferenceRepository.saveAutoLoginState(isChecked)
        _isAutoLoginState.value = isChecked
    }

    fun setLoadingState(isLoading: Boolean) {
        _isLoadingView.value = isLoading
    }
}
