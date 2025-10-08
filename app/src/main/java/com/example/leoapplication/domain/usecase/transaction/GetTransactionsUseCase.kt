package com.example.leoapplication.domain.usecase.transaction

import com.example.leoapplication.data.model.Transaction
import com.example.leoapplication.domain.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(userId: String): Result<List<Transaction>> {
        return repository.getUserTransactions(userId)
    }
}