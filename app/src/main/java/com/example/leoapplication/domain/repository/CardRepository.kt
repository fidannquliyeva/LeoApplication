package com.example.leoapplication.domain.repository

import com.example.leoapplication.data.model.Card


interface CardRepository {

    /**
     * İstifadəçinin kartlarını almaq
     */
    suspend fun getUserCards(userId: String): Result<List<Card>>

    /**
     * Kart ID-sinə görə kartı almaq
     */
    suspend fun getCardById(cardId: String): Result<Card?>

    /**
     * Kart balansını yeniləmək
     */
    suspend fun updateCardBalance(cardId: String, newBalance: Double): Result<Unit>

    suspend fun getCardByNumber(cardNumber: String): Result<Card>

    suspend fun toggleCardStatus(cardId: String, isActive: Boolean): Result<Unit>
}