package com.example.leoapplication.data.model

import com.google.firebase.firestore.DocumentId

data class Payment(
    @DocumentId
    val paymentId: String = "",
    val userId: String = "",
    val cardId: String = "",
    val categoryId: String = "",
    val providerId: String = "",
    val accountNumber: String = "",      // telefon nöm, hesab nöm
    val amount: Double = 0.0,
    val currency: String = "AZN",
    val status: PaymentStatus = PaymentStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)

enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED
}