package com.example.leoapplication.domain.repository

import com.example.leoapplication.domain.model.Transaction

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TransactionRepository(private val firestore: FirebaseFirestore) {
    private val collection = firestore.collection("transactions")

    suspend fun addTransaction(transaction: Transaction) {
        // Auto ID istifadə etmək daha təhlükəsizdir
        collection.add(transaction).await()
    }
}
