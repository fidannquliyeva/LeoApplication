package com.example.leoapplication.data.model

import com.google.firebase.firestore.DocumentId


data class User(
    @DocumentId
    val userId: String = "",
    val phoneNumber: String = "",
    val fullName: String = "",
    val email: String = "",
    val finCode: String = "",
    val birthDate: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isVerified: Boolean = false
)
