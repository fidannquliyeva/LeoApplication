package com.example.leoapplication.domain.model

data class User(
    val uid: String = "",
    var fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val cards: List<String> = emptyList() ,// Kart nömrələri
//    val avatar: String = ""
)
