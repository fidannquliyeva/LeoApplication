package com.example.leoapplication.domain.model

import java.io.Serializable
import java.time.LocalDateTime

data class Transaction(
    val id: Int,
    val transactionNumber: String,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val iconRes: Int,
    val receiverCardId: Int,
    val senderCardId: Int,
    val transactionDate: LocalDateTime,
    val transactionStatus: String,
    val paymentId: Int,
    val transactionIcon: String

) : Serializable
