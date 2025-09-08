package com.example.leoapplication.domain.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class Transaction(
    val id: Int,
    val transactionNumber: String,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val iconRes: Int,
    val receiverCardId: String?, // 🔹 String?
    val senderCardId: String,    // 🔹 String
    val transactionDate: com.google.firebase.Timestamp = Timestamp.now(),
    val transactionStatus: String,
    val paymentId: Int,
    val transactionIcon: String,
    val userId: String
) : Serializable
