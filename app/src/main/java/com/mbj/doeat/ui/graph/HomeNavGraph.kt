package com.mbj.doeat.ui.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mbj.doeat.R
import com.mbj.doeat.ui.screen.home.setting.SettingScreen
import com.mbj.doeat.ui.screen.home.chat.ChatScreen
import com.mbj.doeat.ui.screen.home.community.PostListScreen
import com.mbj.doeat.ui.screen.home.menu_recommendation.MenuRecommendationScreen
import com.mbj.doeat.ui.screen.home.nearby_restaurants.NearbyRestaurantsScreen

@Composable
fun HomeNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.NearbyRestaurants.route
    ) {
        composable(route = BottomBarScreen.NearbyRestaurants.route) {
            NearbyRestaurantsScreen(
                name = BottomBarScreen.NearbyRestaurants.route,
                onClick = { }
            )
        }
        composable(route = BottomBarScreen.Chat.route) {
            ChatScreen(
                name = BottomBarScreen.Chat.route,
                onClick = { }
            )
        }
        composable(route = BottomBarScreen.Community.route) {
            PostListScreen(
                name = BottomBarScreen.Community.route,
                onClick = { }
            )
        }
        composable(route = BottomBarScreen.MenuRecommendation.route) {
            MenuRecommendationScreen(
                name = BottomBarScreen.MenuRecommendation.route,
                onClick = { }
            )
        }
        composable(route = BottomBarScreen.Setting.route) {
            SettingScreen(
                name = BottomBarScreen.Setting.route,
                onClick = { }
            )
        }
    }
}

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: Int
) {
    object NearbyRestaurants : BottomBarScreen(
        route = "NEARBY_RESTAURANTS",
        title = "주변 맛집",
        icon = R.drawable.restaurant_icon
    )

    object Chat : BottomBarScreen(
        route = "CHAT",
        title = "채팅",
        icon = R.drawable.chat_icon
    )

    object Community : BottomBarScreen(
        route = "COMMUNITY",
        title = "파티 모집",
        icon = R.drawable.party_icon
    )

    object MenuRecommendation : BottomBarScreen(
        route = "MENU_RECOMMENDATION",
        title = "메뉴 추천",
        icon = R.drawable.recommendation_icon
    )

    object Setting : BottomBarScreen(
        route = "SETTING",
        title = "설정",
        icon = R.drawable.setting_icon
    )
}
