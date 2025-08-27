package com.example.leoapplication.data.repository

import com.example.leoapplication.domain.model.BankCard
import com.example.leoapplication.domain.repository.BankCardRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BankCardRepositoryImpl(
    private val firestore: FirebaseFirestore
) : BankCardRepository {

    private val collection = firestore.collection("bankCards")

    override suspend fun getCardByPhone(phone: String): BankCard? {
        val snapshot = collection.whereEqualTo("ownerPhone", phone).get().await()
        return if (!snapshot.isEmpty) snapshot.documents[0].toObject(BankCard::class.java)
        else null
    }

    override suspend fun createCard(card: BankCard) {
        collection.document(card.ownerPhone).set(card).await()
    }
}