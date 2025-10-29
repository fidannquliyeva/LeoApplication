package com.example.leoapplication.data.repository

import com.example.leoapplication.data.model.Share
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ShareRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("shares")

    suspend fun getShares(): List<Share> {
        return try {
            val shares = collection.get().await()
            shares.documents.mapNotNull { it.toObject(Share::class.java) }
        } catch (e: Exception){
            e.printStackTrace()
            emptyList()
        }
    }
}