package com.example.leoapplication.data.repository

import com.example.leoapplication.domain.model.BankCard
import com.example.leoapplication.domain.repository.BankCardRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// BankCardRepositoryImpl.kt
class BankCardRepositoryImpl(private val firestore: FirebaseFirestore) : BankCardRepository {
    private val collection = firestore.collection("bankCards")

    override suspend fun getCardByPhone(phone: String) =
        collection.whereEqualTo("ownerPhone", phone).get().await()
            .documents.firstOrNull()?.toObject(BankCard::class.java)

    override suspend fun createCard(card: BankCard) {
        collection.document(card.cardNumber).set(card).await()
    }
}
