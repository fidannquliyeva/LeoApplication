package com.example.leoapplication.data.repository

import com.example.leoapplication.domain.model.BankCard
import com.example.leoapplication.domain.repository.BankCardRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// BankCardRepositoryImpl.kt
class BankCardRepositoryImpl(private val firestore: FirebaseFirestore) : BankCardRepository {
    private val collection = firestore.collection("bankCards")

    //id 0 yarat autogenerate her collectiona elave ele manual id set ele

    override suspend fun getCardByPhone(phone: String) =
        collection.whereEqualTo("ownerPhone", phone).get().await()
            .documents.firstOrNull()?.toObject(BankCard::class.java)

    override suspend fun createCard(card: BankCard) {
        collection.document(card.cardNumber).set(card).await()
    }


    suspend fun updateCardByNumber(
        amount: Double,
        senderCardId: String,
        receiverCardId: String?
    ): Boolean {
        return try {
            val senderDoc = collection.document(senderCardId).get().await()
            val senderBalance = senderDoc.getDouble("balance") ?: 0.0
            collection.document(senderCardId).update("balance", senderBalance - amount).await()

            if (receiverCardId != null) {
                val receiverDoc = collection.document(receiverCardId).get().await()
                val receiverBalance = receiverDoc.getDouble("balance") ?: 0.0
                collection.document(receiverCardId).update("balance", receiverBalance + amount).await()
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}


