package com.mbj.doeat.ui.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mbj.doeat.ui.screen.signin.SignInScreen


fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        route = "AUTHENTICATION",
        startDestination = AuthScreen.Login.route
    ) {
        composable(route = AuthScreen.Login.route) {
            SignInScreen(navHostController = navController)
        }
    }
}

sealed class AuthScreen(val route: String) {
    object Login : AuthScreen(route = "LOGIN")
}
