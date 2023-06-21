package com.example.vibees.screens.home

import android.annotation.SuppressLint
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vibees.graphs.HomeNavGraph
import com.example.vibees.screens.bottombar.BottomBarScreen


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = { BottomBarScreen(navController = navController) },
    ) {
        HomeNavGraph(navController = navController)
    }
}

