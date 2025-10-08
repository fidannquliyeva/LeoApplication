package com.example.leoapplication.domain.repository

import com.example.leoapplication.data.model.Payment
import com.example.leoapplication.data.model.PaymentCategory
import com.example.leoapplication.data.model.ServiceProvider


interface PaymentRepository {

    /**
     * Bütün ödəniş kateqoriyalarını almaq
     */
    suspend fun getPaymentCategories(): Result<List<PaymentCategory>>

    /**
     * Kateqoriyaya görə provayderləri almaq
     */
    suspend fun getProvidersByCategory(categoryId: String): Result<List<ServiceProvider>>

    /**
     * Ödəniş etmək
     */
    suspend fun makePayment(
        userId: String,
        cardId: String,
        providerId: String,
        accountNumber: String,
        amount: Double
    ): Result<Payment>

    /**
     * İstifadəçinin ödəniş tarixçəsini almaq
     */
    suspend fun getPaymentHistory(userId: String): Result<List<Payment>>
}