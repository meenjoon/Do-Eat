package com.mbj.doeat.ui.graph

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.SearchItem
import com.mbj.doeat.ui.screen.home.detail.detail_home.DetailScreen
import com.mbj.doeat.ui.screen.home.detail.detail_writer.DetailWriterScreen
import com.mbj.doeat.util.SerializationUtils

fun NavGraphBuilder.detailsNavGraph(navController: NavHostController) {
    composable(
        route = DetailScreen.Detail.routeWithArgName(),
        arguments = DetailScreen.Detail.arguments
    ) { navBackStackEntry ->
        val searchItem = DetailScreen.Detail.findArgument<SearchItem>(navBackStackEntry)
        DetailScreen(
            searchItem = searchItem!!,
            navController = navController,
            onClick = { }
        )
    }

    composable(
        route = DetailScreen.DetailWriter.routeWithArgName(),
        arguments = DetailScreen.DetailWriter.arguments
    ) { navBackStackEntry ->
        val party = DetailScreen.DetailWriter.findArgument<Party>(navBackStackEntry)
        DetailWriterScreen(
            party = party!!,
            navController = navController,
            onClick = { }
        )
    }
}

sealed class DetailScreen(val route: String, val argName: String) {
    object Detail : DetailScreen(route = "DETAIL", argName = "searchItem")
    object DetailWriter : DetailScreen(route = "DETAIL_WRITER", argName = "searchItem")

    val arguments: List<NamedNavArgument> = listOf(
        navArgument(argName) { type = NavType.StringType }
    )

    fun routeWithArgName(): String {
        return "$route/{$argName}"
    }

    inline fun <reified T> navigateWithArg(argValue: T?): String {
        val arg = SerializationUtils.toJson(argValue)
        return "$route/$arg"
    }

    inline fun <reified T> findArgument(navBackStackEntry: NavBackStackEntry): T? {
        val searchItemString = navBackStackEntry.arguments?.getString(argName)
        return SerializationUtils.fromJson<T>(searchItemString)
    }
}
