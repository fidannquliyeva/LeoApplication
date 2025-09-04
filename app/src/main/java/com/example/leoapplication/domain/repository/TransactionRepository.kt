package com.example.leoapplication.domain.repository

import com.example.leoapplication.domain.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore

class TransactionRepository(private val firestore: FirebaseFirestore)  {
    val collection = firestore.collection("transactions")
    suspend fun addTransaction(transaction: Transaction,onResult: (Boolean) -> Unit){
        collection.document(transaction.id.toString())
            .set(transaction)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }


    }

}