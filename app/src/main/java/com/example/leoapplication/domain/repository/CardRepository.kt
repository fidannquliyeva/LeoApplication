package com.example.leoapplication.domain.repository

import com.example.leoapplication.data.model.Card


interface CardRepository {


    suspend fun getUserCards(userId: String): Result<List<Card>>

    suspend fun getCardById(cardId: String): Result<Card?>

    suspend fun updateCardBalance(cardId: String, newBalance: Double): Result<Unit>

    suspend fun getCardByNumber(cardNumber: String): Result<Card>

    suspend fun toggleCardStatus(cardId: String, isActive: Boolean): Result<Unit>
}