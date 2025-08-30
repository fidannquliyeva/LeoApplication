package com.example.leoapplication.domain.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val cards: List<String> = emptyList() ,// Kart nömrələri
//    val avatar: String = ""
)
