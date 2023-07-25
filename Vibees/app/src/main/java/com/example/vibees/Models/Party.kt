package com.example.vibees.Models

import java.time.LocalDateTime
import java.util.UUID

data class Party (
    val party_id: String?,
    val party_avatar_url: String,
    val name: String?,
    val date_time: String,
    val host_id: UUID?,
    val max_cap: Int,
    val desc: String,
    val entry_fee: Double,
    val type: String?,
    val drug: Boolean,
    val byob: Boolean,
    val host_name: String?,
    val qr_endpoint: String?,
    val street: String,
    val city: String,
    val prov: String,
    val postal_code: String,
    val tags: List<String>,
)