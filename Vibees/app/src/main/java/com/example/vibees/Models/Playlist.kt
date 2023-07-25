package com.example.vibees.Models

data class Playlist (
    val user: User,
    val songList: List<String>? = null
)