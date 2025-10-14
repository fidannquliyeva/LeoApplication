package com.example.leoapplication.data.remote

import android.util.Log
import com.example.leoapplication.data.model.Card
import com.example.leoapplication.data.model.Transaction
import com.example.leoapplication.data.model.TransactionStatus
import com.example.leoapplication.data.model.User
import com.example.leoapplication.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // ==================== USER OPERATIONS ====================

    suspend fun createUser(user: User): Result<Unit> {
        return try {
            firestore.collection(Constants.USERS_COLLECTION)
                .document(user.userId)
                .set(user)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(userId: String): Result<User?> {
        return try {
            val snapshot = firestore.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            val user = snapshot.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(userId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .update(updates)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserByPhoneNumber(phoneNumber: String): Result<User?> {
        return try {
            val snapshot = firestore.collection(Constants.USERS_COLLECTION)
                .whereEqualTo("phoneNumber", phoneNumber)
                .limit(1)
                .get()
                .await()

            val user = if (!snapshot.isEmpty) {
                snapshot.documents[0].toObject(User::class.java)
            } else {
                null
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== CARD OPERATIONS ====================

    suspend fun createCard(card: Card): Result<Unit> {
        return try {
            firestore.collection(Constants.CARDS_COLLECTION)
                .document(card.cardId)
                .set(card)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserCards(userId: String): Result<List<Card>> {
        return try {
            val snapshot = firestore.collection(Constants.CARDS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("isActive", true)
                .get()
                .await()

            val cards = snapshot.toObjects(Card::class.java)
            Result.success(cards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCardBalance(cardId: String, newBalance: Double): Result<Unit> {
        return try {
            firestore.collection(Constants.CARDS_COLLECTION)
                .document(cardId)
                .update("balance", newBalance)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCardById(cardId: String): Result<Card?> {
        return try {
            val snapshot = firestore.collection(Constants.CARDS_COLLECTION)
                .document(cardId)
                .get()
                .await()

            val card = snapshot.toObject(Card::class.java)
            Result.success(card)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleCardStatus(cardId: String, isActive: Boolean): Result<Unit> {
        return try {
            firestore.collection(Constants.CARDS_COLLECTION)
                .document(cardId)
                .update("isActive", isActive)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCardByNumber(cardNumber: String): Result<Card> {
        return try {
            Log.d("FirestoreDataSource", "Searching card: $cardNumber")

            var snapshot = firestore.collection(Constants.CARDS_COLLECTION)
                .whereEqualTo("cardNumber", cardNumber)
                .limit(1)
                .get()
                .await()

            if (snapshot.isEmpty) {
                val formattedCardNumber = cardNumber.chunked(4).joinToString(" ")
                Log.d("FirestoreDataSource", "Trying with spaces: $formattedCardNumber")

                snapshot = firestore.collection(Constants.CARDS_COLLECTION)
                    .whereEqualTo("cardNumber", formattedCardNumber)
                    .limit(1)
                    .get()
                    .await()
            }

            if (snapshot.isEmpty) {
                Log.e("FirestoreDataSource", "❌ Card not found: $cardNumber")
                Result.failure(Exception("Kart tapılmadı"))
            } else {
                val card = snapshot.documents[0].toObject(Card::class.java)
                if (card != null) {
                    Log.d("FirestoreDataSource", "✅ Card found: ${card.cardId}")
                    Result.success(card)
                } else {
                    Result.failure(Exception("Kart məlumatı oxuna bilmədi"))
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreDataSource", "❌ Error: ${e.message}")
            Result.failure(e)
        }
    }

    // ==================== TRANSACTION OPERATIONS ====================

    suspend fun createTransaction(transaction: Transaction): Result<Unit> {
        return try {
            firestore.collection(Constants.TRANSACTIONS_COLLECTION)
                .document(transaction.transactionId)
                .set(transaction)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserTransactions(userId: String): Result<List<Transaction>> {
        return try {
            val sentSnapshot = firestore.collection(Constants.TRANSACTIONS_COLLECTION)
                .whereEqualTo("fromUserId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val receivedSnapshot = firestore.collection(Constants.TRANSACTIONS_COLLECTION)
                .whereEqualTo("toUserId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val sentTransactions = sentSnapshot.toObjects(Transaction::class.java)
            val receivedTransactions = receivedSnapshot.toObjects(Transaction::class.java)

            val allTransactions = (sentTransactions + receivedTransactions)
                .sortedByDescending { it.timestamp }

            Log.d("FirestoreDataSource", "✅ Total transactions: ${allTransactions.size}")
            Log.d("FirestoreDataSource", "  - Sent: ${sentTransactions.size}")
            Log.d("FirestoreDataSource", "  - Received: ${receivedTransactions.size}")

            Result.success(allTransactions)
        } catch (e: Exception) {
            Log.e("FirestoreDataSource", "❌ Error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * ✅ YENİ - Real-time transaction observer
     */
    /**
     * ✅ Real-time transaction observer - həm sent həm received
     */
    fun observeUserTransactions(userId: String): Flow<Result<List<Transaction>>> = callbackFlow {
        Log.d("FirestoreDataSource", "Starting to observe transactions for: $userId")

        var sentTransactions: List<Transaction> = emptyList()
        var receivedTransactions: List<Transaction> = emptyList()

        fun emitCombinedTransactions() {
            val allTransactions = (sentTransactions + receivedTransactions)
                .distinctBy { it.transactionId }
                .sortedByDescending { it.timestamp }

            Log.d("FirestoreDataSource", "✅ Combined transactions: ${allTransactions.size}")
            Log.d("FirestoreDataSource", "  - Sent: ${sentTransactions.size}")
            Log.d("FirestoreDataSource", "  - Received: ${receivedTransactions.size}")

            allTransactions.forEach { tx ->
                Log.d("FirestoreDataSource", "    [${tx.type}] ${tx.amount} - ${tx.description}")
            }

            trySend(Result.success(allTransactions))
        }

        val sentListener = firestore.collection(Constants.TRANSACTIONS_COLLECTION)
            .whereEqualTo("fromUserId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreDataSource", "❌ Sent listener error: ${error.message}")
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                sentTransactions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Transaction::class.java)?.copy(transactionId = doc.id)
                } ?: emptyList()

                Log.d("FirestoreDataSource", "Sent transactions updated: ${sentTransactions.size}")
                emitCombinedTransactions()
            }

        val receivedListener = firestore.collection(Constants.TRANSACTIONS_COLLECTION)
            .whereEqualTo("toUserId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreDataSource", "❌ Received listener error: ${error.message}")
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                receivedTransactions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Transaction::class.java)?.copy(transactionId = doc.id)
                } ?: emptyList()

                Log.d("FirestoreDataSource", "Received transactions updated: ${receivedTransactions.size}")
                emitCombinedTransactions()
            }

        awaitClose {
            Log.d("FirestoreDataSource", "Closing transaction listeners")
            sentListener.remove()
            receivedListener.remove()
        }
    }

    /**
     * ✅ YENİ - Transaction silmək
     */
    suspend fun deleteTransaction(transactionId: String): Result<Unit> {
        return try {
            Log.d("FirestoreDataSource", "Deleting transaction: $transactionId")

            firestore.collection(Constants.TRANSACTIONS_COLLECTION)
                .document(transactionId)
                .delete()
                .await()

            Log.d("FirestoreDataSource", "✅ Transaction deleted: $transactionId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreDataSource", "❌ Delete failed: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * ✅ YENİ - Transaction-ı geri yükləmək (Undo)
     */
    suspend fun restoreTransaction(transaction: Transaction): Result<Unit> {
        return try {
            Log.d("FirestoreDataSource", "Restoring transaction: ${transaction.transactionId}")

            firestore.collection(Constants.TRANSACTIONS_COLLECTION)
                .document(transaction.transactionId)
                .set(transaction)
                .await()

            Log.d("FirestoreDataSource", "✅ Transaction restored: ${transaction.transactionId}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreDataSource", "❌ Restore failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateTransactionStatus(
        transactionId: String,
        status: TransactionStatus
    ): Result<Unit> {
        return try {
            Log.d("FirestoreDataSource", "Updating transaction status: $transactionId -> $status")

            firestore.collection(Constants.TRANSACTIONS_COLLECTION)
                .document(transactionId)
                .update("status", status.name)
                .await()

            Log.d("FirestoreDataSource", "✅ Status updated")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreDataSource", "❌ Status update failed: ${e.message}")
            Result.failure(e)
        }
    }

    // ==================== CARD GENERATION UTILITIES ====================

    fun generateFullCard(phoneNumber: String, fullName: String): Card {
        val cardNumber = generateCardNumber(phoneNumber)
        val cvv = generateCVV()
        val expiryDate = generateExpiryDate()
        val cardType = determineCardType(cardNumber)

        return Card(
            cardNumber = cardNumber,
            cardHolderName = fullName.uppercase(),
            cvv = cvv,
            expiryDate = expiryDate,
            cardType = cardType,
            balance = Constants.DEFAULT_BALANCE,
            currency = Constants.DEFAULT_CURRENCY,
            isActive = true
        )
    }

    fun generateCardNumber(phoneNumber: String): String {
        val bin = "4098"
        val phoneDigits = phoneNumber.filter { it.isDigit() }.takeLast(8)
        val randomDigits = (1000..9999).random().toString()
        val first15 = bin + phoneDigits.take(8) + randomDigits.take(3)
        val checkDigit = calculateLuhnCheckDigit(first15)
        val fullNumber = first15 + checkDigit

        return "${fullNumber.substring(0, 4)} ${fullNumber.substring(4, 8)} ${fullNumber.substring(8, 12)} ${fullNumber.substring(12)}"
    }

    private fun calculateLuhnCheckDigit(number: String): Int {
        var sum = 0
        var alternate = true

        for (i in number.length - 1 downTo 0) {
            var digit = number[i].toString().toInt()
            if (alternate) {
                digit *= 2
                if (digit > 9) {
                    digit = (digit % 10) + 1
                }
            }
            sum += digit
            alternate = !alternate
        }

        return (10 - (sum % 10)) % 10
    }

    fun generateCVV(): String {
        return kotlin.random.Random.nextInt(100, 999).toString()
    }

    fun generateExpiryDate(): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.YEAR, 5)

        val month = calendar.get(java.util.Calendar.MONTH) + 1
        val year = calendar.get(java.util.Calendar.YEAR) % 100

        return String.format("%02d/%02d", month, year)
    }

    private fun determineCardType(cardNumber: String): String {
        val firstDigits = cardNumber.replace(" ", "").take(4)
        return when {
            firstDigits.startsWith("4") -> "VISA"
            firstDigits.startsWith("5") -> "MasterCard"
            firstDigits.startsWith("62") -> "UnionPay"
            else -> "VISA"
        }
    }
}