package com.example.leoapplication.data.model

import com.google.firebase.firestore.DocumentId

data class Card(
    @DocumentId
    val cardId: String = "",
    val userId: String = "",
    val cardNumber: String = "",
    val cardHolderName: String = "",
    val cvv: String = "",
    val expiryDate: String = "",
    val cardType: String = "VISA",
    val balance: Double = 0.0,
    val currency: String = "AZN",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true

)
{
    // Maskalanmış kart nömrəsi
    fun getMaskedCardNumber(): String {
        return if (cardNumber.length >= 4) {
            "**** **** **** ${cardNumber.takeLast(4)}"
        } else {
            cardNumber
        }
    }

    // Format edilmiş kart nömrəsi (4-4-4-4)
    fun getFormattedCardNumber(): String {
        val digitsOnly = cardNumber.replace(" ", "")
        return if (digitsOnly.length == 16) {
            "${digitsOnly.substring(0, 4)} ${digitsOnly.substring(4, 8)} ${digitsOnly.substring(8, 12)} ${digitsOnly.substring(12)}"
        } else {
            cardNumber
        }
    }

    // Balans formatlanmış
    fun getFormattedBalance(): String {
        return String.format("%.2f", balance)
    }

    // Kartın müddəti bitib?
    fun isExpired(): Boolean {
        if (expiryDate.isEmpty()) return false

        try {
            val parts = expiryDate.split("/")
            if (parts.size != 2) return false

            val month = parts[0].toInt()
            val year = 2000 + parts[1].toInt()

            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1

            return year < currentYear || (year == currentYear && month < currentMonth)
        } catch (e: Exception) {
            return false
        }
    }
}
