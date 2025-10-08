package com.example.leoapplication.data.repository

import com.example.leoapplication.data.model.Transaction
import com.example.leoapplication.data.model.TransactionStatus
import com.example.leoapplication.data.model.TransactionType
import com.example.leoapplication.data.remote.FirestoreDataSource
import com.example.leoapplication.domain.repository.TransactionRepository
import com.example.leoapplication.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : TransactionRepository {

    override suspend fun transferMoney(
        fromUserId: String,
        toPhoneNumber: String,
        amount: Double,
        description: String
    ): Result<Transaction> {
        return try {
            // 1. Alıcı istifadəçini tap
            val recipientResult = firestoreDataSource.getUserByPhoneNumber(toPhoneNumber)
            if (recipientResult.isFailure || recipientResult.getOrNull() == null) {
                return Result.failure(Exception("Alıcı tapılmadı"))
            }
            val recipient = recipientResult.getOrNull()!!

            // 2. Göndərənin kartını tap
            val senderCardsResult = firestoreDataSource.getUserCards(fromUserId)
            if (senderCardsResult.isFailure || senderCardsResult.getOrNull()?.isEmpty() == true) {
                return Result.failure(Exception("Kart tapılmadı"))
            }
            val senderCard = senderCardsResult.getOrNull()!!.first()

            // 3. Balans yoxla
            if (senderCard.balance < amount) {
                return Result.failure(Exception("Balans kifayət deyil"))
            }

            // 4. Alıcının kartını tap
            val recipientCardsResult = firestoreDataSource.getUserCards(recipient.userId)
            if (recipientCardsResult.isFailure || recipientCardsResult.getOrNull()?.isEmpty() == true) {
                return Result.failure(Exception("Alıcının kartı tapılmadı"))
            }
            val recipientCard = recipientCardsResult.getOrNull()!!.first()

            // 5. Transaksiya yarat
            val transactionId = FirebaseFirestore.getInstance()
                .collection(Constants.TRANSACTIONS_COLLECTION)
                .document()
                .id

            val transaction = Transaction(
                transactionId = transactionId,
                fromUserId = fromUserId,
                toUserId = recipient.userId,
                fromCardId = senderCard.cardId,
                toCardId = recipientCard.cardId,
                amount = amount,
                currency = Constants.DEFAULT_CURRENCY,
                type = TransactionType.TRANSFER,
                status = TransactionStatus.PENDING,
                description = description
            )

            // 6. Transaksiyani Firestore-a yaz
            val createResult = firestoreDataSource.createTransaction(transaction)
            if (createResult.isFailure) {
                return Result.failure(createResult.exceptionOrNull()!!)
            }

            // 7. Balansları yenilə
            val newSenderBalance = senderCard.balance - amount
            val newRecipientBalance = recipientCard.balance + amount

            firestoreDataSource.updateCardBalance(senderCard.cardId, newSenderBalance)
            firestoreDataSource.updateCardBalance(recipientCard.cardId, newRecipientBalance)

            // 8. Transaksiya statusunu COMPLETED et
            firestoreDataSource.updateTransactionStatus(transactionId, TransactionStatus.COMPLETED)

            Result.success(transaction.copy(status = TransactionStatus.COMPLETED))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserTransactions(userId: String): Result<List<Transaction>> {
        return firestoreDataSource.getUserTransactions(userId)
    }

    override suspend fun updateTransactionStatus(
        transactionId: String,
        status: TransactionStatus
    ): Result<Unit> {
        return firestoreDataSource.updateTransactionStatus(transactionId, status)
    }
}