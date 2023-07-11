package com.example.vibees.graphs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vibees.screens.GenericScreen
import com.example.vibees.screens.bottombar.BottomBar
import com.example.vibees.screens.home.myparties.MyPartiesScreen
import com.example.vibees.screens.user.UserScreen

@Composable
fun HomeNavGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBar.Home.route
    ) {
        composable(route = BottomBar.Home.route) {
            UserScreen(
                onClick = {id ->
                    navController.navigate(PartyScreen.ViewingDetails.passId(id))
                },
                modifier = modifier
            )
        }
        composable(route = BottomBar.MyParties.route) {
            MyPartiesScreen(
                onClick = {id ->
                    navController.navigate(PartyScreen.Details.passId(id))
                },
                modifier = modifier
            )
        }
        composable(route = BottomBar.Host.route) {
            GenericScreen(
                name = BottomBar.Host.route,
                onClick = { navController.navigate(HostScreens.Step1.route) {
                    launchSingleTop = true
                } }
            )
//            HostScreen(
//                name = BottomBar.Host.route,
//                onClick = { }
//            )
        }
        composable(route = BottomBar.Settings.route) {
            GenericScreen(
                name = BottomBar.Settings.route,
                onClick = { navController.navigate(SettingsScreen.Setting1.route) }
            )
        }
        composable(route = BottomBar.Help.route) {
            GenericScreen(
                name = BottomBar.Help.route,
                onClick = { }
            )
        }
        partyNavGraph(navController = navController)
        hostNavGraph(navController = navController)
        settingsNavGraph(navController = navController)
    }
}

