package com.example.leoapplication.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

enum class PaymentStatus(val value: String) {
    PENDING("pending"),
    PAID("paid"),
    FAILED("failed")
}

enum class PaymentType(val value: String) {
    USERTOUSER("user to user"),
    UTILITY("utility")

}

data class Payment(
    val id: Int,
    val paymentNumber: String,
    val amount: Double,
    var paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val paymentDate: LocalDateTime,
    val senderCardId: String, // 🔹 indi String
    val receiverCardId: String?, // 🔹 indi String?
    val paymentType: PaymentType,
    val paymentTitle: String,
    val subscriberNumber: String?,
    val userId: String // Firebase UID
)

