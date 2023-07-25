package com.example.vibees.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vibees.screens.home.HomeScreen
import kotlinx.coroutines.CompletableDeferred


@Composable
fun RootNavigationGraph(navController: NavHostController, signIn: suspend (signInComplete: CompletableDeferred<Unit>) -> Unit, navigateToHome: Boolean) {
    // Check if navigateToHome is true, and if so, navigate to the Home screen directly
    if (navigateToHome) {
        NavHost(
            navController = navController,
            route = Graph.ROOT,
            startDestination = Graph.HOME // Set the Home screen as the start destination
        ) {
            composable(route = Graph.HOME) {
                HomeScreen()
            }
            // Add other composables for the rest of your screens if needed
        }
    } else {
        // If navigateToHome is false, follow the regular navigation flow
        NavHost(
            navController = navController,
            route = Graph.ROOT,
            startDestination = Graph.AUTHENTICATION
        ) {
            authNavGraph(signIn, navController = navController)
            composable(route = Graph.HOME) {
                HomeScreen()
            }
            // Add other composables for the rest of your screens if needed
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
