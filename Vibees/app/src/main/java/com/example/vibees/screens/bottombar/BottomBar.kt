package com.example.vibees.screens.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBar(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home: BottomBar(
        route = "HOME",
        title = "HOME",
        icon = Icons.Default.Home
    )
    object MyParties: BottomBar(
        route = "MYPARTIES",
        title = "MYPARTIES",
        icon = Icons.Default.Favorite
    )
    object Host: BottomBar(
        route = "HOST",
        title = "HOST",
        icon = Icons.Default.AddCircle
    )
    object Settings: BottomBar(
        route = "SETTINGS",
        title = "SETTINGS",
        icon = Icons.Default.Settings
    )
    object Help: BottomBar(
        route = "HELP",
        title = "HELP",
        icon = Icons.Default.Call
    )
}
