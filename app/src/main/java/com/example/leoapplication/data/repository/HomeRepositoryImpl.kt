package com.example.leoapplication.data.repository

import com.example.leoapplication.data.model.Card
import com.example.leoapplication.data.model.User
import com.example.leoapplication.domain.repository.HomeRepository
import com.example.leoapplication.util.Resource
import com.example.leoapplication.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : HomeRepository {

    override suspend fun getUserData(): Resource<User> {
        return try {
            val userId = auth.currentUser?.uid

            if (userId == null) {
                return Resource.Error("İstifadəçi tapılmadı")
            }

            val document = firestore.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                val user = document.toObject(User::class.java)
                if (user != null) {
                    Resource.Success(user)
                } else {
                    Resource.Error("İstifadəçi məlumatları oxunmadı")
                }
            } else {
                Resource.Error("İstifadəçi tapılmadı")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Xəta baş verdi")
        }
    }

    override fun observeUserCards(): Flow<Resource<List<Card>>> = callbackFlow {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            trySend(Resource.Error("İstifadəçi tapılmadı"))
            close()
            return@callbackFlow
        }

        // Real-time listener
        val listener = firestore.collection(Constants.CARDS_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Xəta baş verdi"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val cards = snapshot.toObjects(Card::class.java)
                    trySend(Resource.Success(cards))
                } else {
                    trySend(Resource.Error("Məlumat tapılmadı"))
                }
            }

        // Flow bağlandıqda listener-i sil
        awaitClose { listener.remove() }
    }

    override suspend fun getUserCards(): Resource<List<Card>> {
        return try {
            val userId = auth.currentUser?.uid

            if (userId == null) {
                return Resource.Error("İstifadəçi tapılmadı")
            }

            val snapshot = firestore.collection(Constants.CARDS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("isActive", true)
                .get()
                .await()

            val cards = snapshot.toObjects(Card::class.java)

            Resource.Success(cards)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Xəta baş verdi")
        }
    }
}