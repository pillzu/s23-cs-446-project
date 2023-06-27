package com.example.vibees.screens.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBar(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home: BottomBar(
        route = "HOME",
        title = "Home",
        icon = Icons.Default.Home
    )
    object MyParties: BottomBar(
        route = "MYPARTIES",
        title = "Parties",
        icon = Icons.Default.Favorite
    )
    object Host: BottomBar(
        route = "HOST",
        title = "Host",
        icon = Icons.Default.AddCircle
    )
    object Settings: BottomBar(
        route = "SETTINGS",
        title = "Setting",
        icon = Icons.Default.Settings
    )
    object Help: BottomBar(
        route = "HELP",
        title = "Help",
        icon = Icons.Default.Info
    )
}
