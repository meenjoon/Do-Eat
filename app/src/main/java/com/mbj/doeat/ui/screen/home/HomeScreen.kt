package com.mbj.doeat.ui.screen.home

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mbj.doeat.ui.graph.BottomBarScreen
import com.mbj.doeat.ui.graph.HomeNavGraph
import com.mbj.doeat.ui.theme.Yellow700
import com.mbj.doeat.ui.theme.Gray200

@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) {
        HomeNavGraph(navController = navController)
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.NearbyRestaurants,
        BottomBarScreen.Chat,
        BottomBarScreen.Community,
        BottomBarScreen.MenuRecommendation,
        BottomBarScreen.Setting,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = screens.any { it.route == currentDestination?.route }
    if (bottomBarDestination) {
        BottomNavigation(
            backgroundColor = Yellow700,
            contentColor = Color.Black
        ) {
            val backStackEntry = navController.currentBackStackEntryAsState()
            screens.forEach { screen ->
                val currentRoute = backStackEntry.value?.destination?.route;
                val selected = currentRoute == screen.route
                AddItem(
                    screen = screen,
                    navController = navController,
                    selected = selected
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    navController: NavHostController,
    selected: Boolean
) {
    BottomNavigationItem(
        label = {
            Text(
                if (selected) screen.title else "",
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        },
        icon = {
            Icon(
                painter = painterResource(id = screen.icon),
                contentDescription = "Navigation Icon",
                tint = if (selected) Color.Black else Gray200
            )
        },
        selected = selected,
        onClick = {
            navController.navigate(screen.route) {
                navController.graph.startDestinationRoute?.let {
                    popUpTo(it) {
                        saveState = true
                    }
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}
