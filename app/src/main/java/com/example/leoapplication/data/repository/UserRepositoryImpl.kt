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

    override suspend fun getUserByPhone(phone: String): User? {
        val snapshot = firestore.collection("users")
            .whereEqualTo("phone", phone)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.toObject(User::class.java)
    }


    override suspend fun createUser(user: User) {
        collection.document(user.phone).set(user).await()
    }

    override suspend fun addCardToUser(phone: String, cardNumber: String) {
        collection.document(phone).update("cards", FieldValue.arrayUnion(cardNumber)).await()
    }


//    suspend fun updateUserAvatar(phone: String, avatarUrl: String) {
//        firestore.collection("users")
//            .document(phone)
//            .update("avatar", avatarUrl)
//            .await()
//    }

}
