package com.example.leoapplication.domain.repository

import com.example.leoapplication.domain.model.User

interface UserRepository {
    suspend fun getUserByUid(uid: String): User?
    suspend fun createUser(user: User)
    suspend fun addCardToUser(uid: String, cardNumber: String)


}
