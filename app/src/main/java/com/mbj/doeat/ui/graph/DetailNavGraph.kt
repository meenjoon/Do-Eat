package com.mbj.doeat.ui.graph

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mbj.doeat.data.remote.model.SearchItem
import com.mbj.doeat.ui.screen.home.detail.DetailScreen
import com.mbj.doeat.util.SerializationUtils

fun NavGraphBuilder.detailsNavGraph(navController: NavHostController) {
    composable(
        route = DetailScreen.Detail.routeWithArgName(),
        arguments = DetailScreen.Detail.arguments
    ) { navBackStackEntry ->
        val searchItem = DetailScreen.Detail.findArgument(navBackStackEntry)
        DetailScreen(
            searchItem = searchItem!!,
            navController = navController,
            onClick = { }
        )
    }
}

sealed class DetailScreen(val route: String, val argName: String) {
    object Detail : DetailScreen(route = "DETAIL", argName = "searchItem")

    val arguments: List<NamedNavArgument> = listOf(
        navArgument(argName) { type = NavType.StringType }
    )

    fun routeWithArgName(): String {
        return "$route/{$argName}"
    }

    fun navigateWithArg(argValue: SearchItem?): String {
        val arg = SerializationUtils.toJson(argValue)
        return "$route/$arg"
    }

    fun findArgument(navBackStackEntry: NavBackStackEntry): SearchItem? {
        val searchItemString = navBackStackEntry.arguments?.getString(argName)
        return SerializationUtils.fromJson<SearchItem>(searchItemString)
    }
}
