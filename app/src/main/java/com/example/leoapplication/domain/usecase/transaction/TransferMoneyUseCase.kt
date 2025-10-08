package com.example.leoapplication.domain.usecase.transaction

import com.example.leoapplication.data.model.Transaction
import com.example.leoapplication.domain.repository.TransactionRepository
import javax.inject.Inject

class TransferMoneyUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(
        fromUserId: String,
        toPhoneNumber: String,
        amount: Double,
        description: String
    ): Result<Transaction> {
        // Validasiya
        if (amount <= 0) {
            return Result.failure(Exception("Məbləğ 0-dan böyük olmalıdır"))
        }

        if (toPhoneNumber.isEmpty()) {
            return Result.failure(Exception("Telefon nömrəsi daxil edin"))
        }

        return repository.transferMoney(fromUserId, toPhoneNumber, amount, description)
    }
}