package com.example.leoapplication.domain.repository

import com.example.leoapplication.domain.model.User

interface UserRepository {
    suspend fun getUserByPhone(phone: String): User?
    suspend fun createUser(user: User)
    suspend fun addCardToUser(phone: String, cardNumber: String)


}
