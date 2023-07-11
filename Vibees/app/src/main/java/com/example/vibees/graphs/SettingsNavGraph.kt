package com.example.vibees.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.vibees.screens.GenericScreen


fun NavGraphBuilder.settingsNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.SETTINGS,
        startDestination = SettingsScreen.Setting1.route
    ) {
        composable(
            route = SettingsScreen.Setting1.route
        ) {
            GenericScreen(
                name = "Setting1",
                onClick = { /* can navigate to other page here (use navController) */ }
            )
        }
        // add other composable sections for each additional page
        // see other graphs for reference
    }
}

sealed class SettingsScreen(val route: String) {
    object Setting1 : SettingsScreen(route = "Setting1")
    // add other pages here as an object to access the route
}