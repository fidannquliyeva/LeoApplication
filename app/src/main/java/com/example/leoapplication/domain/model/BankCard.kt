package com.example.leoapplication.domain.model

data class BankCard(
    val cardNumber: String = "",
    val cvv: String = "",
    val mm: String = "",
    val yy: String = "",
    val balance: Double = 0.0,
    val ownerPhone: String = ""
)