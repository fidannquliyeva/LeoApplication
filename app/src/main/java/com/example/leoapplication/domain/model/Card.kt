package com.example.leoapplication.domain.model

data class Card(
    val cardId: String = "",
    val cardNumber: String = "",
    val cvv: String = "",
    val expiryDate: String = "", // MM/YY formatında
    val balance: Double = 0.0,
    val currency: String = "AZN",
    val createdAt: com.google.firebase.Timestamp? = null
)


