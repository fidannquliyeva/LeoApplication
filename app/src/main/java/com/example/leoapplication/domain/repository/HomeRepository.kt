package com.example.leoapplication.domain.repository

import com.example.leoapplication.data.model.Card
import com.example.leoapplication.data.model.User
import com.example.leoapplication.util.Resource
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    suspend fun getUserData(): Resource<User>

    fun observeUserCards(): Flow<Resource<List<Card>>>

    suspend fun getUserCards(): Resource<List<Card>>


    suspend fun increaseBalanceWithTransaction(
        cardId: String,
        amount: Double,
        externalCardLast4: String
    ): Resource<Unit>

}