package com.example.leoapplication.domain.usecase.payment

import com.example.leoapplication.data.model.Payment
import com.example.leoapplication.domain.repository.PaymentRepository
import javax.inject.Inject

class GetPaymentHistoryUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(userId: String): Result<List<Payment>> {
        return repository.getPaymentHistory(userId)
    }
}