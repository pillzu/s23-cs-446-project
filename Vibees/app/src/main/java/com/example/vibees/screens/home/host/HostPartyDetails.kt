package com.example.vibees.screens.home.host

import me.naingaungluu.formconductor.annotations.Form
import me.naingaungluu.formconductor.annotations.Optional

@Form
data class HostPartyDetails(

    val name: String = "",

    val theme: String = "",

    @Optional
    val fee: Number = 0,

    val description: String = "",

    val drugfriendly: String = "",

    val byob: String = ""
)
