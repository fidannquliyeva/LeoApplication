package com.example.leoapplication.data.repository

import android.util.Log
import com.example.leoapplication.data.model.Transaction
import com.example.leoapplication.data.model.TransactionStatus
import com.example.leoapplication.data.model.TransactionType
import com.example.leoapplication.data.remote.FirestoreDataSource
import com.example.leoapplication.domain.repository.TransactionRepository
import com.example.leoapplication.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val firestore: FirebaseFirestore // ✅ Batch write üçün lazımdır
) : TransactionRepository {

    override suspend fun transferMoney(
        fromUserId: String,
        toPhoneNumber: String,
        amount: Double,
        description: String
    ): Result<Transaction> {
        return try {
            Log.d("TransactionRepo", "====== TRANSFER START ======")
            Log.d("TransactionRepo", "From: $fromUserId")
            Log.d("TransactionRepo", "To: $toPhoneNumber")
            Log.d("TransactionRepo", "Amount: $amount")

            // 1. Alıcı istifadəçini tap
            val recipientResult = firestoreDataSource.getUserByPhoneNumber(toPhoneNumber)
            if (recipientResult.isFailure || recipientResult.getOrNull() == null) {

                return Result.failure(Exception("Alıcı tapılmadı"))
            }
            val recipient = recipientResult.getOrNull()!!

            // 2. Göndərənin kartını tap
            val senderCardsResult = firestoreDataSource.getUserCards(fromUserId)
            if (senderCardsResult.isFailure || senderCardsResult.getOrNull()?.isEmpty() == true) {
                Log.e("TransactionRepo", "Sender card not found")
                return Result.failure(Exception("Kart tapılmadı"))
            }
            val senderCard = senderCardsResult.getOrNull()!!.first()

            // 3. Balans yoxla
            if (senderCard.balance < amount) {
                Log.e("TransactionRepo", "insufficient balance")
                return Result.failure(Exception("Balans kifayət deyil"))
            }

            // 4. Alıcının kartını tap
            val recipientCardsResult = firestoreDataSource.getUserCards(recipient.userId)
            if (recipientCardsResult.isFailure || recipientCardsResult.getOrNull()?.isEmpty() == true) {
                Log.e("TransactionRepo", " Recipient card not found")
                return Result.failure(Exception("Alıcının kartı tapılmadı"))
            }
            val recipientCard = recipientCardsResult.getOrNull()!!.first()

            // 5. Yeni balanslar
            val newSenderBalance = senderCard.balance - amount
            val newRecipientBalance = recipientCard.balance + amount

            // 6. Transaction yarat
            val transactionId = UUID.randomUUID().toString()
            val transaction = Transaction(
                transactionId = transactionId,
                fromUserId = fromUserId,
                toUserId = recipient.userId,
                fromCardId = senderCard.cardId,
                toCardId = recipientCard.cardId,
                amount = amount,
                currency = Constants.DEFAULT_CURRENCY,
                type = TransactionType.TRANSFER,
                status = TransactionStatus.COMPLETED,
                description = description.ifEmpty { "Karta köçürmə" },
                timestamp = System.currentTimeMillis()
            )

            // 7. BATCH WRITE (atomic)
            firestore.runBatch { batch ->
                val senderRef = firestore.collection(Constants.CARDS_COLLECTION)
                    .document(senderCard.cardId)
                batch.update(senderRef, "balance", newSenderBalance)

                val receiverRef = firestore.collection(Constants.CARDS_COLLECTION)
                    .document(recipientCard.cardId)
                batch.update(receiverRef, "balance", newRecipientBalance)

                val transactionRef = firestore.collection(Constants.TRANSACTIONS_COLLECTION)
                    .document(transactionId)
                batch.set(transactionRef, transaction)
            }.await()

            Result.success(transaction)
        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    override suspend fun getUserTransactions(userId: String): Result<List<Transaction>> {
        return try {
            firestoreDataSource.getUserTransactions(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeUserTransactions(userId: String): Flow<Result<List<Transaction>>> {
        return firestoreDataSource.observeUserTransactions(userId)
    }

    override suspend fun updateTransactionStatus(
        transactionId: String,
        status: TransactionStatus
    ): Result<Unit> {
        return firestoreDataSource.updateTransactionStatus(transactionId, status)
    }

    override suspend fun deleteTransaction(transactionId: String): Result<Unit> {
        return firestoreDataSource.deleteTransaction(transactionId)
    }

    override suspend fun restoreTransaction(transaction: Transaction): Result<Unit> {
        return firestoreDataSource.restoreTransaction(transaction)
    }
}