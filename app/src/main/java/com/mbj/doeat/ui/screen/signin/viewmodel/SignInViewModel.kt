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

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val defaultDBRepository: DefaultDBRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {

    fun signIn(
        loginRequest: LoginRequest,
        navHostController: NavHostController
    ) {
        viewModelScope.launch {
            defaultDBRepository.signIn(loginRequest,
                onComplete = {
                },
                onError = {
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
                if (tokenInfo != null) {
                    val isAutoLogin = userPreferenceRepository.getSaveAutoLoginState()
                    if (isAutoLogin) {
                        findUserAndNavigateHome(tokenInfo.id!!, navHostController)
                    }
                }
            }
        }
    }

    private fun findUserAndNavigateHome(kakaoUserId: Long, navHostController: NavHostController) {
        viewModelScope.launch {
            defaultDBRepository.findUser(
                findUserRequest = FindUserRequest(kakaoUserId),
                onComplete = {
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

    fun setAutoLoginEnabled(boolean: Boolean) {
        userPreferenceRepository.saveAutoLoginState(boolean)
    }

    fun isAutoLoginEnabled(): Boolean {
        return userPreferenceRepository.getSaveAutoLoginState()
    }
}
