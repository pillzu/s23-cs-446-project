package com.example.vibees

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.vibees.graphs.RootNavigationGraph
import com.example.vibees.ui.theme.VibeesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VibeesTheme {
                RootNavigationGraph(navController = rememberNavController())
            }
        }
    }
}