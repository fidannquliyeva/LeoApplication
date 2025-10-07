package com.example.leoapplication.data.model

import com.google.firebase.firestore.DocumentId

data class PaymentCategory(
    @DocumentId
    val categoryId: String = "",
    val name: String = "",              // "Mobil operatorlar", "Kommunal"
    val iconUrl: String = "",
    val isActive: Boolean = true
)