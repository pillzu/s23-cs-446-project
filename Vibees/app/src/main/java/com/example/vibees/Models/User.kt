package com.example.vibees.Models

import android.net.Uri
import java.util.UUID

data class User(
    val user_id: UUID,
    val profile_url: Uri? = Uri.parse("https://media.istockphoto.com/id/871547412/photo/here-to-secure-my-future.jpg?s=612x612&w=0&k=20&c=u4bCPxz4eO5JeWdcDHgTwFzzgbAftMdVxPH-tcfjA0c="),
    val first_name: String? = "",
    val last_name: String? = "",
    val phone_no: Int? = 0,
    val address_street: String? = "",
    val address_city: String? = "",
    val address_prov: String? = "",
    val address_postal: String? = "",
    val email: String? = "",
    val partyPoints: Int? = 0
)
