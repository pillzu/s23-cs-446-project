package com.example.vibees.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.vibees.screens.GenericScreen
import com.example.vibees.screens.auth.LoginScreen
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun NavGraphBuilder.authNavGraph(signIn: suspend (signInComplete: CompletableDeferred<Unit>) -> Unit, navController: NavHostController) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthScreen.Login.route
    ) {
        composable(route = AuthScreen.Login.route) {
            LoginScreen(
                onClick = {
                    navController.popBackStack()
                    navController.navigate(Graph.HOME)
                },
                onSignUpClick = {
                    navController.navigate(AuthScreen.SignUp.route)
                },
                onForgotClick = {
                    navController.navigate(AuthScreen.Forgot.route)
                },
                onCreateAccountClick = {
                    val signInComplete = CompletableDeferred<Unit>()
                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO) {
                            signIn(signInComplete)
                        }

                        signInComplete.await()
                        navController.popBackStack()
                        navController.navigate(Graph.HOME)
                    }
                }
            )
        }
        composable(route = AuthScreen.SignUp.route) {
            GenericScreen(name = AuthScreen.SignUp.route) {}
        }
        composable(route = AuthScreen.Forgot.route) {
            GenericScreen(name = AuthScreen.Forgot.route) {}
        }
    }
}

sealed class AuthScreen(val route: String) {
    object Login: AuthScreen(route = "LOGIN")
    object SignUp: AuthScreen(route = "SIGN_UP")
    object Forgot : AuthScreen(route = "FORGOT")
}