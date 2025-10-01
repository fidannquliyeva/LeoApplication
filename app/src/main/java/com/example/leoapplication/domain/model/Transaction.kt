package com.example.leoapplication.domain.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class Transaction(
    val transactionId: String = "",
    val fromCardId: String = "",
    val toCardId: String = "",
    val amount: Double = 0.0,
    val currency: String = "AZN",
    val type: String = "", // "payment", "received", "transfer"
    val createdAt: Timestamp? = null
)