package com.example.vibees.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vibees.screens.GenericScreen
import com.example.vibees.screens.bottombar.BottomBar
import com.example.vibees.screens.home.myparties.MyPartiesScreen

@Composable
fun HomeNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBar.Home.route
    ) {
        composable(route = BottomBar.Home.route) {
            GenericScreen(
                name = BottomBar.Home.route,
                onClick = { }
            )
        }
        composable(route = BottomBar.MyParties.route) {
            MyPartiesScreen(
                onClick = {id ->
                    navController.navigate(PartyScreen.Details.passId(id))
                }
            )
        }
        composable(route = BottomBar.Host.route) {
            GenericScreen(
                name = BottomBar.Host.route,
                onClick = { }
            )
        }
        composable(route = BottomBar.Settings.route) {
            GenericScreen(
                name = BottomBar.Settings.route,
                onClick = { }
            )
        }
        composable(route = BottomBar.Help.route) {
            GenericScreen(
                name = BottomBar.Help.route,
                onClick = { }
            )
        }
        partyNavGraph(navController = navController)
    }
}

