package com.example.leoapplication.domain.model

data class User(
    val uid: String = "",
    val firstName: String = "",
    val email: String = "",
    val phone: String = "",
    val createdAt: Any? = null // FieldValue.serverTimestamp() üçün
)

