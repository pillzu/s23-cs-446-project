package com.example.vibees

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.vibees.Models.Party
import com.example.vibees.Models.User
import java.util.UUID

object GlobalAppState {
    var UserID by mutableStateOf<UUID?>(null)
    var UserName by mutableStateOf<String?>(null)
    var PartyDetails by mutableStateOf<Party?>(null)
    var currentUser by mutableStateOf<User?>(null)
}

