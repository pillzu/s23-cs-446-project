package com.example.vibees.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vibees.graphs.HomeNavGraph
import com.example.vibees.screens.bottombar.BottomBarScreen
import com.example.vibees.ui.theme.SubtleWhite


@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = { BottomBarScreen(navController = navController) },
        containerColor = SubtleWhite,
        contentColor = Color.Black
    ) {PaddingValues ->
        HomeNavGraph(navController = navController, modifier = Modifier.padding(PaddingValues))
    }
}

