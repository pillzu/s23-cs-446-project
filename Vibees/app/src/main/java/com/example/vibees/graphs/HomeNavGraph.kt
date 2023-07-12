package com.example.vibees.graphs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vibees.screens.GenericScreen
import com.example.vibees.screens.bottombar.BottomBar
import com.example.vibees.screens.home.HostScreen
import com.example.vibees.screens.home.myparties.MyPartiesScreen
import com.example.vibees.screens.user.UserScreen
import com.example.vibees.screens.home.HelpScreen

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
            HostScreen(
                name = BottomBar.Host.route,
                onClick = { }
            )
        }
        composable(route = BottomBar.Settings.route) {
            GenericScreen(
                name = BottomBar.Settings.route,
                onClick = { navController.navigate(SettingsScreen.Setting1.route) }
            )
        }
        composable(route = BottomBar.Help.route) {
            HelpScreen(
                onClickAboutUs = {
                    // Handle About Us click
                    // For example, show a dialog with information about the company
                    showDialog("About Us", "This is our company information.")
                },
                onClickFAQ = {
                    // Handle FAQ click
                    // For example, navigate to an FAQ screen
                    showDialog("FAQ", "Question 1?\\n   Answer 1\\n\\n2. Question 2?\\n   Answer 2")
                },
                onClickPrivacyPolicy = {
                    // Handle Privacy Policy click
                    // For example, show a dialog with the privacy policy
                    showDialog("Privacy Policy", "This is our privacy policy.")
                }
            )
        }

        partyNavGraph(navController = navController)
        settingsNavGraph(navController = navController)
    }
}

