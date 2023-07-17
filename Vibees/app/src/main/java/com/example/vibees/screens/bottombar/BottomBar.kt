package com.example.vibees.screens.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.res.painterResource
import com.example.vibees.R

sealed class BottomBar(
    val route: String,
    val title: String,
    val icon: Int
) {
    object Home: BottomBar(
        route = "HOME",
        title = "Home",
        icon = R.drawable.home
    )
    object MyParties: BottomBar(
        route = "MYPARTIES",
        title = "Parties",
        icon = R.drawable.party_icon,
    )
    object Host: BottomBar(
        route = "HOST",
        title = "Host",
        icon = R.drawable.add_icon,
    )
    object Settings: BottomBar(
        route = "SETTINGS",
        title = "Setting",
        icon = R.drawable.settings_icon,
    )
    object Help: BottomBar(
        route = "HELP",
        title = "Help",
        icon = R.drawable.contact
    )
}
