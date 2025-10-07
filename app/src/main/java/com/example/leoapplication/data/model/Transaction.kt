package com.example.leoapplication.data.model

import com.google.firebase.firestore.DocumentId

data class Transaction(
    @DocumentId
    val transactionId: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val fromCardId: String = "",
    val toCardId: String = "",
    val amount: Double = 0.0,
    val currency: String = "AZN",
    val type: TransactionType = TransactionType.TRANSFER,
    val status: TransactionStatus = TransactionStatus.PENDING,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

enum class TransactionType {
    TRANSFER,      // İstifadəçidən istifadəçiyə
    PAYMENT,       // Xidmət ödənişi
    DEPOSIT,       // Balans artırma
    WITHDRAWAL     // Balans çıxarma
}

enum class TransactionStatus {
    PENDING,       // Gözləyir
    COMPLETED,     // Tamamlandı
    FAILED,        // Uğursuz
    CANCELLED      // Ləğv edildi
}