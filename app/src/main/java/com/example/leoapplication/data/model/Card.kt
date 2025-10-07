package com.example.leoapplication.data.model

import com.google.firebase.firestore.DocumentId


data class Card(
    @DocumentId
    val cardId: String = "",
    val userId: String = "",
    val cardNumber: String = "",
    val balance: Double = 0.0,
    val currency: String = "AZN",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
