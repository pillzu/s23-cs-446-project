package com.example.vibees.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vibees.graphs.HomeNavGraph
import com.example.vibees.screens.bottombar.BottomBarScreen


@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = { BottomBarScreen(navController = navController) },
    ) {PaddingValues ->
        HomeNavGraph(navController = navController, modifier = Modifier.padding(PaddingValues))
    }
}

