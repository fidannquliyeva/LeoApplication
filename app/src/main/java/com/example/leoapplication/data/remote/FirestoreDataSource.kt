package com.example.leoapplication.data.remote

import android.provider.SyncStateContract
import com.example.leoapplication.data.model.Card
import com.example.leoapplication.data.model.Transaction
import com.example.leoapplication.data.model.User
import com.example.leoapplication.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // ==================== USER OPERATIONS ====================

    /**
     * İstifadəçi profili yaratmaq
     */
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

    /**
     * İstifadəçi məlumatlarını oxumaq
     */
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

    /**
     * İstifadəçi məlumatlarını yeniləmək
     */
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

    /**
     * Telefon nömrəsi ilə istifadəçi tapmaq
     */
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

    /**
     * Kart yaratmaq
     */
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

    /**
     * İstifadəçinin kartlarını oxumaq
     */
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

    /**
     * Kart balansını yeniləmək
     */
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

    /**
     * Kart ID-sinə görə kartı oxumaq
     */
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

    // ==================== TRANSACTION OPERATIONS ====================

    /**
     * Transaksiya yaratmaq
     */
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

    /**
     * İstifadəçinin transaksiyalarını oxumaq
     */
    suspend fun getUserTransactions(userId: String): Result<List<Transaction>> {
        return try {
            val snapshot = firestore.collection(Constants.TRANSACTIONS_COLLECTION)
                .whereEqualTo("fromUserId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val transactions = snapshot.toObjects(Transaction::class.java)
            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Transaksiya statusunu yeniləmək
     */
    suspend fun updateTransactionStatus(
        transactionId: String,
        status: com.example.leoapplication.data.model.TransactionStatus
    ): Result<Unit> {
        return try {
            firestore.collection(Constants.TRANSACTIONS_COLLECTION)
                .document(transactionId)
                .update("status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== UTILITY ====================
// ==================== CARD GENERATION UTILITIES ====================

    /**
     * Tam kart məlumatları ilə yeni kart generasiya et
     */
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

    /**
     * Kart nömrəsi generasiya (Luhn algoritmi ilə)
     */
    fun generateCardNumber(phoneNumber: String): String {
        val bin = "5169"
        val phoneDigits = phoneNumber.filter { it.isDigit() }.takeLast(8)
        val randomDigits = (1000..9999).random().toString()
        val first15 = bin + phoneDigits.take(8) + randomDigits.take(3)
        val checkDigit = calculateLuhnCheckDigit(first15)
        val fullNumber = first15 + checkDigit

        return "${fullNumber.substring(0, 4)} ${fullNumber.substring(4, 8)} ${fullNumber.substring(8, 12)} ${fullNumber.substring(12)}"
    }

    /**
     * Luhn check digit hesabla
     */
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

    /**
     * CVV generasiya (3 rəqəmli)
     */
    fun generateCVV(): String {
        return kotlin.random.Random.nextInt(100, 999).toString()
    }

    /**
     * Expiry date generasiya (indidən 5 il sonra)
     */
    fun generateExpiryDate(): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.YEAR, 5)

        val month = calendar.get(java.util.Calendar.MONTH) + 1
        val year = calendar.get(java.util.Calendar.YEAR) % 100

        return String.format("%02d/%02d", month, year)
    }

    /**
     * Kart nömrəsinə görə kart tipini təyin et
     */
    private fun determineCardType(cardNumber: String): String {
        val firstDigits = cardNumber.replace(" ", "").take(4)
        return when {
            firstDigits.startsWith("4") -> "VISA"
            firstDigits.startsWith("5") -> "MasterCard"
            firstDigits.startsWith("62") -> "UnionPay"
            else -> "VISA"
        }
    }


    // ✅ YENİ - Block/Unblock kart
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
}