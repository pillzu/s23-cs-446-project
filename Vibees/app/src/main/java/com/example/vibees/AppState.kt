package com.example.vibees

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object GlobalAppState {
    var UserID by mutableStateOf("6515c9f8-57f1-406f-8707-20033dcd764e")
    var UserName by mutableStateOf("Christian")
}

