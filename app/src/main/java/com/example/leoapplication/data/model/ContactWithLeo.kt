package com.example.leoapplication.data.model

data class ContactWithLeo(
    val name: String,
    val phoneNumber: String,
    val userId: String,
    val hasLeoAccount: Boolean = true,
    val lastTransferDate: Long? = null
)