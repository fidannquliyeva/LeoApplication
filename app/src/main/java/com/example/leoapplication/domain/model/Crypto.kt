package com.example.leoapplication.domain.model

data class Crypto(
    val id: String = "",
    val name: String = "",
    val symbol: String = "",
    val logoUrl: String = "",
    val priceUsd: Double = 0.0
)
