package com.example.leoapplication.domain.model
import com.google.firebase.Timestamp

data class User(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val fcmToken: String? = null,
    val createdAt: Timestamp? = null
)

