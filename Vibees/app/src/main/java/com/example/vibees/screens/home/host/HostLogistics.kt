package com.example.vibees.screens.home.host

import me.naingaungluu.formconductor.annotations.Form
import me.naingaungluu.formconductor.annotations.MaxLength
import me.naingaungluu.formconductor.annotations.MinLength

@Form
data class HostLogistics(

    val city: String = "",

    val unitandstreet: String = "",

    @MinLength(6)
    @MaxLength(6)
    val postalcode: String = "",

    val province: String = "",

    val date: String = "",

    val time: String = ""

)
