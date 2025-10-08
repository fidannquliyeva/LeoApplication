package com.example.leoapplication.data.repository

import com.example.leoapplication.data.model.Payment
import com.example.leoapplication.data.model.PaymentCategory
import com.example.leoapplication.data.model.PaymentStatus
import com.example.leoapplication.data.model.ServiceProvider
import com.example.leoapplication.data.remote.FirestoreDataSource
import com.example.leoapplication.data.remote.PaymentDataSource
import com.example.leoapplication.domain.repository.PaymentRepository
import com.example.leoapplication.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val paymentDataSource: PaymentDataSource,
    private val firestoreDataSource: FirestoreDataSource
) : PaymentRepository {

    override suspend fun getPaymentCategories(): Result<List<PaymentCategory>> {
        return paymentDataSource.getPaymentCategories()
    }

    override suspend fun getProvidersByCategory(categoryId: String): Result<List<ServiceProvider>> {
        return paymentDataSource.getProvidersByCategory(categoryId)
    }

    override suspend fun makePayment(
        userId: String,
        cardId: String,
        providerId: String,
        accountNumber: String,
        amount: Double
    ): Result<Payment> {
        return try {
            // 1. Kartı tap və balansı yoxla
            val cardResult = firestoreDataSource.getCardById(cardId)
            if (cardResult.isFailure || cardResult.getOrNull() == null) {
                return Result.failure(Exception("Kart tapılmadı"))
            }

            val card = cardResult.getOrNull()!!
            if (card.balance < amount) {
                return Result.failure(Exception("Balans kifayət deyil"))
            }

            // 2. Payment yarat
            val paymentId = FirebaseFirestore.getInstance()
                .collection("payments")
                .document()
                .id

            val payment = Payment(
                paymentId = paymentId,
                userId = userId,
                cardId = cardId,
                categoryId = "",  // Provider-dən alınacaq
                providerId = providerId,
                accountNumber = accountNumber,
                amount = amount,
                currency = Constants.DEFAULT_CURRENCY,
                status = PaymentStatus.PENDING
            )

            // 3. Payment-i Firestore-a yaz
            val createResult = paymentDataSource.createPayment(payment)
            if (createResult.isFailure) {
                return Result.failure(createResult.exceptionOrNull()!!)
            }

            // 4. Balansı azalt
            val newBalance = card.balance - amount
            firestoreDataSource.updateCardBalance(cardId, newBalance)

            Result.success(payment.copy(status = PaymentStatus.COMPLETED))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPaymentHistory(userId: String): Result<List<Payment>> {
        return paymentDataSource.getPaymentHistory(userId)
    }
}