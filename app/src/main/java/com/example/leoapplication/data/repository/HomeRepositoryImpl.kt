package com.example.leoapplication.data.repository

import android.util.Log
import com.example.leoapplication.data.model.Card
import com.example.leoapplication.data.model.Transaction           // ✅ ƏLAVƏ ET
import com.example.leoapplication.data.model.TransactionType       // ✅ ƏLAVƏ ET
import com.example.leoapplication.data.model.TransactionStatus     // ✅ ƏLAVƏ ET
import com.example.leoapplication.data.model.User
import com.example.leoapplication.data.remote.FirestoreDataSource
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
    private val auth: FirebaseAuth,
    private val firestoreDataSource: FirestoreDataSource
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

    // ✅ YENİ METOD - Transaction ilə balans artırma
    override suspend fun increaseBalanceWithTransaction(
        cardId: String,
        amount: Double,
        externalCardLast4: String
    ): Resource<Unit> {
        return try {
            Log.d("HomeRepository", "====== BALANCE INCREASE START ======")
            Log.d("HomeRepository", "CardId: $cardId")
            Log.d("HomeRepository", "Amount: $amount")
            Log.d("HomeRepository", "External Card: *$externalCardLast4")

            // 1. Kartı oxu
            val cardResult = firestoreDataSource.getCardById(cardId)
            if (cardResult.isFailure || cardResult.getOrNull() == null) {
                Log.e("HomeRepository", "❌ Card not found")
                return Resource.Error("Kart tapılmadı")
            }
            val card = cardResult.getOrNull()!!
            Log.d("HomeRepository", "Current balance: ${card.balance}")

            // 2. Yeni balans
            val newBalance = card.balance + amount
            Log.d("HomeRepository", "New balance will be: $newBalance")

            // 3. Transaction yarat
            val transactionId = java.util.UUID.randomUUID().toString()
            val transaction = Transaction(
                transactionId = transactionId,
                fromUserId = "EXTERNAL_BANK", // Xarici bank
                toUserId = auth.currentUser?.uid ?: "",
                fromCardId = "CARD_*$externalCardLast4", // Xarici kart
                toCardId = card.cardId,
                amount = amount,
                currency = Constants.DEFAULT_CURRENCY,
                type = TransactionType.BALANCE_INCREASE, // ✅ YENİ TİP
                status = TransactionStatus.COMPLETED,
                description = "Balans artırma (*$externalCardLast4)",
                timestamp = System.currentTimeMillis()
            )

            Log.d("HomeRepository", "Transaction created: $transactionId")

            // 4. ✅ BATCH WRITE - hər ikisini birdən et
            firestore.runBatch { batch ->
                // Balansı artır
                val cardRef = firestore.collection(Constants.CARDS_COLLECTION)
                    .document(card.cardId)
                batch.update(cardRef, "balance", newBalance)
                Log.d("HomeRepository", "Batch: Update balance")

                // Transaction-ı save et
                val transactionRef = firestore.collection(Constants.TRANSACTIONS_COLLECTION)
                    .document(transactionId)
                batch.set(transactionRef, transaction)
                Log.d("HomeRepository", "Batch: Save transaction")
            }.await()

            Log.d("HomeRepository", "✅✅✅ BALANCE INCREASE SUCCESSFUL ✅✅✅")
            Resource.Success(Unit)

        } catch (e: Exception) {
            Log.e("HomeRepository", "❌❌❌ BALANCE INCREASE FAILED ❌❌❌")
            Log.e("HomeRepository", "Error: ${e.message}", e)
            Resource.Error(e.message ?: "Xəta baş verdi")
        }
    }
}