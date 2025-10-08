package com.example.leoapplication.domain.usecase.payment

import com.example.leoapplication.data.model.Payment
import com.example.leoapplication.domain.repository.PaymentRepository
import javax.inject.Inject

class MakePaymentUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(
        userId: String,
        cardId: String,
        providerId: String,
        accountNumber: String,
        amount: Double
    ): Result<Payment> {
        // Validasiya
        if (amount <= 0) {
            return Result.failure(Exception("Məbləğ 0-dan böyük olmalıdır"))
        }

        if (accountNumber.isEmpty()) {
            return Result.failure(Exception("Hesab nömrəsi daxil edin"))
        }

        return repository.makePayment(userId, cardId, providerId, accountNumber, amount)
    }
}