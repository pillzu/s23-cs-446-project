package com.example.vibees.screens.home.host

import me.naingaungluu.formconductor.annotations.Form

@Form
data class HostPartyDetails(

    val name: String = "",

    val theme: String = "",

    val fee: String = "",

    val capacity: String = "",

    val description: String = "",

    val drugfriendly: String = "",

    val byob: String = ""
)
