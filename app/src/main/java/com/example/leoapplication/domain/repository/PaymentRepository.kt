package com.example.leoapplication.domain.repository

import com.example.leoapplication.data.model.Payment
import com.example.leoapplication.data.model.PaymentCategory
import com.example.leoapplication.data.model.ServiceProvider


interface PaymentRepository {

    suspend fun getPaymentCategories(): Result<List<PaymentCategory>>

    suspend fun getProvidersByCategory(categoryId: String): Result<List<ServiceProvider>>


    suspend fun makePayment(
        userId: String,
        cardId: String,
        providerId: String,
        accountNumber: String,
        amount: Double
    ): Result<Payment>

    suspend fun getPaymentHistory(userId: String): Result<List<Payment>>
}