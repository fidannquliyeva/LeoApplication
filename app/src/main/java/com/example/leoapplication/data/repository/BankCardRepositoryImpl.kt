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

    suspend fun updateCardByNumber(amount: Double,senderCardId: String, receiverCardId: String,
                                    onResult: (Boolean) -> Unit) {


        collection.document(senderCardId)
            .get()
            .addOnSuccessListener {
                document-> if (document.exists()){


                        val balance = document.getDouble("balance")
                        document.reference.update("balance", balance?.minus(amount))
                            .addOnSuccessListener {
                                onResult(true)
                            }
                            .addOnFailureListener {
                                onResult(false)

                    }
                }

            }

        collection.document(receiverCardId)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {


                    val balance = document.getDouble("balance")
                    document.reference.update("balance", balance?.plus(amount))
                        .addOnSuccessListener {
                            onResult(true)
                        }
                        .addOnFailureListener {
                            onResult(false)

                        }
                }


            }}
}
