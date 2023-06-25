package com.example.vibees.Models

import java.util.Date

data class Party (
    val user_id: Int,
    val party_name: String,
    val date_time: Date?,
    val type: String,
    val max_capacity: Int,
    val entry_fees: Double,
    val desc: String,
    val street: String,
    val city: String,
    val province: String,
    val postal_code: String,
)