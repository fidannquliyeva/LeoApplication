package com.example.leoapplication.data.repository

import com.example.leoapplication.data.model.Card
import com.example.leoapplication.data.remote.FirestoreDataSource
import com.example.leoapplication.domain.repository.CardRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CardRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : CardRepository {

    override suspend fun getUserCards(userId: String): Result<List<Card>> {
        return firestoreDataSource.getUserCards(userId)
    }

    override suspend fun getCardById(cardId: String): Result<Card?> {
        return firestoreDataSource.getCardById(cardId)
    }

    override suspend fun updateCardBalance(cardId: String, newBalance: Double): Result<Unit> {
        return firestoreDataSource.updateCardBalance(cardId, newBalance)
    }


    override suspend fun getCardByNumber(cardNumber: String): Result<Card> {
        return firestoreDataSource.getCardByNumber(cardNumber)
         }

    override suspend fun toggleCardStatus(cardId: String, isActive: Boolean): Result<Unit> {
        return firestoreDataSource.toggleCardStatus(cardId, isActive)
    }
}