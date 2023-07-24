package com.example.vibees.Models

import java.time.LocalDateTime
import java.util.UUID

data class Party (
    val user_id: UUID?,
    val name: String?,
    val date_time: LocalDateTime,
    val type: String?,
    val max_cap: Int,
    val entry_fee: Double,
    val desc: String,
    val street: String,
    val city: String,
    val prov: String,
    val postal_code: String,
    val drug: Boolean,
    val byob: Boolean,
    val taglist: List<String>,
    //val image: Uri,
    val party_id: String?,
    val host_name: String?,
    val qr_endpoint: String?,
)