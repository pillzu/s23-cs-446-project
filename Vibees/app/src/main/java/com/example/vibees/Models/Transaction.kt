package com.example.vibees.Models

import java.util.Currency

data class Transaction(
    val amount: Int,
    val currency: String = "cad"
)
