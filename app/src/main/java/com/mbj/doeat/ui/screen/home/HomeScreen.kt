package com.mbj.doeat.ui.screen.home

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.mbj.doeat.ui.graph.BottomBarScreen
import com.mbj.doeat.ui.graph.HomeNavGraph
import com.mbj.doeat.ui.theme.Color.Companion.BottomNavigationSelectedColor
import com.mbj.doeat.ui.theme.Color.Companion.BottomNavigationUnSelectedColor
import com.mbj.doeat.ui.theme.Color.Companion.NormalButtonColor
import com.mbj.doeat.ui.theme.Color.Companion.NormalColorInverted
import com.mbj.doeat.util.NavigationUtils

@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController(),
    fusedLocationClient: FusedLocationProviderClient
) {
    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) { padding ->
        HomeNavGraph(
            padding = padding,
            navController = navController,
            fusedLocationClient = fusedLocationClient
        )
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.NearbyRestaurants,
        BottomBarScreen.ChatRoom,
        BottomBarScreen.Community,
        BottomBarScreen.Setting,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = screens.any { it.route == currentDestination?.route }
    if (bottomBarDestination) {
        BottomNavigation(
            backgroundColor = NormalButtonColor,
            contentColor = NormalColorInverted
        ) {
            val backStackEntry = navController.currentBackStackEntryAsState()
            screens.forEach { screen ->
                val currentRoute = backStackEntry.value?.destination?.route;
                val selected = currentRoute == screen.route
                AddItem(
                    screen = screen,
                    navController = navController,
                    selected = selected,
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
                color = BottomNavigationSelectedColor
            )
        },
        icon = {
            Icon(
                painter = painterResource(id = screen.icon),
                contentDescription = "Navigation Icon",
                tint = if (selected) BottomNavigationSelectedColor else BottomNavigationUnSelectedColor
            )
        },
        selected = selected,
        onClick = {
            NavigationUtils.navigate(controller = navController, routeName = screen.route)
        }
    )
}
