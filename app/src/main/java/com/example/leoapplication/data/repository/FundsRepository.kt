package com.example.leoapplication.data.repository

import com.example.leoapplication.data.model.Funds
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FundsRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("funds")

    suspend fun getFunds(): List<Funds> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Funds::class.java) }
        } catch (e: Exception){
            e.printStackTrace()
            emptyList()
        }
    }
    suspend fun uploadFunds(funds: List<Funds>) {
        funds.forEach { fund ->
            collection.document(fund.id).set(fund)
        }
    }
}
