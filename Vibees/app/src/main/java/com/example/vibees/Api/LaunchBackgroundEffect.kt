package com.example.vibees.Api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LaunchBackgroundEffect(key: Any?, block: suspend () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key) {
        coroutineScope.launch(Dispatchers.Default) {
            block()
        }
    }
}
