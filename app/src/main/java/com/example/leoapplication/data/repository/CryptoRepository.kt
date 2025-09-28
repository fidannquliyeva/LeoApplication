package com.example.leoapplication.data.repository

import com.example.leoapplication.domain.model.Crypto
import com.example.leoapplication.domain.model.Share
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class CryptoRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("cryptos")

    suspend fun getCrypto(): List<Crypto> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Crypto::class.java) }
        } catch (e: Exception){
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun uploadCryptos(cryptos: List<Crypto>) {
        cryptos.forEach { crypto ->
            collection.document(crypto.id).set(crypto)
        }
    }

}