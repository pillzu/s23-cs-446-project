package com.example.vibees.Models

import java.time.LocalDate
import java.util.Date

data class Party (
    val user_id: String?,
    val name: String?,
    val date_time: Date?,
    val type: String?,
    val max_cap: Int,
    val entry_fee: Double,
    val desc: String,
    val street: String,
    val city: String,
    val prov: String,
    val postal_code: String,
    val party_id: String?,
    val host_name: String?,
    val qr_endpoint: String?,
)