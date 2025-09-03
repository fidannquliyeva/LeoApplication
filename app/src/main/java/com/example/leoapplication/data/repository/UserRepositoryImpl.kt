package com.example.leoapplication.data.repository

import com.example.leoapplication.domain.model.User
import com.example.leoapplication.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// UserRepositoryImpl.kt
class UserRepositoryImpl(private val firestore: FirebaseFirestore) : UserRepository {
    private val collection = firestore.collection("users")

    override suspend fun getUserByUid(uid: String): User? {
        return try {
            val snapshot = collection.document(uid).get().await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }



    override suspend fun createUser(user: User) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(user.uid)  // uid ilə saxlayırsan
            .set(user)           // <-- update yox, set()
            .await()
    }

    override suspend fun addCardToUser(uid: String, cardNumber: String) {
        val docRef = collection.document(uid)
        val snapshot = docRef.get().await()
        if (snapshot.exists()) {
            docRef.update("cards", FieldValue.arrayUnion(cardNumber)).await()
        } else {
            // Əgər sənəd yoxdursa, yaradırıq və cards sahəsini əlavə edirik
            docRef.set(mapOf("cards" to listOf(cardNumber))).await()
        }
    }



//    suspend fun updateUserAvatar(phone: String, avatarUrl: String) {
//        firestore.collection("users")
//            .document(phone)
//            .update("avatar", avatarUrl)
//            .await()
//    }

}
