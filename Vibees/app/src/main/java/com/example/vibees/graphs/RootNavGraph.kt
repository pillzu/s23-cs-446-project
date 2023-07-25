package com.example.vibees.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vibees.screens.home.HomeScreen
import kotlinx.coroutines.CompletableDeferred


@Composable
fun RootNavigationGraph(navController: NavHostController, signIn: suspend (signInComplete: CompletableDeferred<Unit>, ) -> Unit, navigateToHome: Boolean) {
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.AUTHENTICATION
    ) {
        if (navigateToHome) {
            navController.navigate(Graph.HOME)
        }
        authNavGraph(signIn, navController = navController)
        composable(route = Graph.HOME) {
            HomeScreen()
        }
    }
}

// Top level graph containing sub graphs
object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val PARTY = "party_graph"
    const val SETTINGS = "settings_graph"
    const val HOST = "host_graph"
}
