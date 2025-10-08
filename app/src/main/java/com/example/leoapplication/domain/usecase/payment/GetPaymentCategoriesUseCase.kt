package com.example.leoapplication.domain.usecase.payment

import com.example.leoapplication.data.model.PaymentCategory
import com.example.leoapplication.domain.repository.PaymentRepository
import javax.inject.Inject

class GetPaymentCategoriesUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(): Result<List<PaymentCategory>> {
        return repository.getPaymentCategories()
    }
}