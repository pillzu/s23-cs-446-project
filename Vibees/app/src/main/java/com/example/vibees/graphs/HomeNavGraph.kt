package com.example.vibees.graphs


import SettingsScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vibees.GlobalAppState
import com.example.vibees.screens.GenericScreen
import com.example.vibees.screens.bottombar.BottomBar
import com.example.vibees.screens.home.myparties.MyPartiesScreen
import com.example.vibees.screens.user.UserScreen
import com.example.vibees.screens.home.HelpScreen
import com.example.vibees.screens.home.host.PartyStore
import com.example.vibees.screens.home.host.HostAgreementScreen

// import settingsNavGraph

@Composable
fun HomeNavGraph(navController: NavHostController, modifier: Modifier) {
    var partystore by GlobalAppState::PartyStore
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
            HostAgreementScreen(onClick = {
                // reset party store to empty
                partystore = PartyStore(isedit = false)
                navController.navigate(HostScreens.Step1.route) {
                launchSingleTop = true
            } })
//            GenericScreen(
//                name = BottomBar.Host.route,
//                onClick = { navController.navigate(HostScreens.Step1.route) {
//                    launchSingleTop = true
//                } }
//            )
//            HostScreen(
//                name = BottomBar.Host.route,
//                onClick = { }
//            )
        }
        composable(route = BottomBar.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(route = BottomBar.Help.route) {
            HelpScreen()
        }

        partyNavGraph(navController = navController)
        hostNavGraph(navController = navController)
        // settingsNavGraph(navController = navController)
    }
}