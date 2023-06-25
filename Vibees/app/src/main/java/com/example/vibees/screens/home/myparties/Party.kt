package com.example.vibees.screens.home.myparties

import androidx.compose.ui.graphics.vector.ImageVector


data class Party(
    val title: String,
    val date: String,
    val time: String,
    val host: String,
    val location: String,
    val icon: ImageVector,
    val entryFee: String
)
