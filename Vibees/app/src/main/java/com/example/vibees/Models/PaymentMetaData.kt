package com.example.vibees.Models

data class PaymentMetaData(
    val paymentIntent: String,
    val ephemeralKey: String,
    val customer: String,
    val publishableKey: String
)
