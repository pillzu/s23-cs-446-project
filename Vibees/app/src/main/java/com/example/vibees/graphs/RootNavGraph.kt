package com.example.vibees.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vibees.screens.home.HomeScreen


@Composable
fun RootNavigationGraph(navController: NavHostController, signIn: () -> Unit) {
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.AUTHENTICATION
    ) {
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
}