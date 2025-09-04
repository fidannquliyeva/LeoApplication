package com.example.leoapplication.domain.model

import java.time.LocalDateTime

enum class PaymentStatus(value: String) {
    PENDING("pending"),
    PAID("paid"),
    FAILED("failed")
}

enum class PaymentType(value: String) {
    USERTOUSER("user to user"),
    UTILITY("utility")

}

data class Payment(
    val id: Int,
    val paymentNumber: String,
    val amount: Double,
    var paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val paymentDate: LocalDateTime,
    val senderCardId: Int,
    val receiverCardId: Int?,
    val paymentType: PaymentType,
    val paymentTitle: String


)
