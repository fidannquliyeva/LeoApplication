package com.example.leoapplication.data.model

import com.google.firebase.firestore.DocumentId

data class ServiceProvider(
    @DocumentId
    val providerId: String = "",
    val categoryId: String = "",
    val name: String = "",               // "Azercell", "Azerişıq"
    val logoUrl: String = "",
    val minAmount: Double = 1.0,
    val maxAmount: Double = 500.0,
    val isActive: Boolean = true
)