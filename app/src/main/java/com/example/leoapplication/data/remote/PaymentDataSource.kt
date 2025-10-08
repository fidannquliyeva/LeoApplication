package com.example.leoapplication.data.remote

import com.example.leoapplication.data.model.Payment
import com.example.leoapplication.data.model.PaymentCategory
import com.example.leoapplication.data.model.ServiceProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    companion object {
        private const val PAYMENT_CATEGORIES_COLLECTION = "payment_categories"
        private const val SERVICE_PROVIDERS_COLLECTION = "service_providers"
        private const val PAYMENTS_COLLECTION = "payments"
    }

    /**
     * Bütün ödəniş kateqoriyalarını almaq
     */
    suspend fun getPaymentCategories(): Result<List<PaymentCategory>> {
        return try {
            val snapshot = firestore.collection(PAYMENT_CATEGORIES_COLLECTION)
                .whereEqualTo("isActive", true)
                .get()
                .await()

            val categories = snapshot.toObjects(PaymentCategory::class.java)
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Kateqoriyaya görə provayderləri almaq
     */
    suspend fun getProvidersByCategory(categoryId: String): Result<List<ServiceProvider>> {
        return try {
            val snapshot = firestore.collection(SERVICE_PROVIDERS_COLLECTION)
                .whereEqualTo("categoryId", categoryId)
                .whereEqualTo("isActive", true)
                .get()
                .await()

            val providers = snapshot.toObjects(ServiceProvider::class.java)
            Result.success(providers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Ödəniş yaratmaq
     */
    suspend fun createPayment(payment: Payment): Result<Unit> {
        return try {
            firestore.collection(PAYMENTS_COLLECTION)
                .document(payment.paymentId)
                .set(payment)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * İstifadəçinin ödəniş tarixçəsini almaq
     */
    suspend fun getPaymentHistory(userId: String): Result<List<Payment>> {
        return try {
            val snapshot = firestore.collection(PAYMENTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val payments = snapshot.toObjects(Payment::class.java)
            Result.success(payments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}