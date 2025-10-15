package com.example.leoapplication.domain.repository

import com.example.leoapplication.data.model.Transaction
import com.example.leoapplication.data.model.TransactionStatus
import kotlinx.coroutines.flow.Flow


interface TransactionRepository {

    suspend fun transferMoney(
        fromUserId: String,
        toPhoneNumber: String,
        amount: Double,
        description: String
    ): Result<Transaction>

    suspend fun getUserTransactions(userId: String): Result<List<Transaction>>

    fun observeUserTransactions(userId: String): Flow<Result<List<Transaction>>>

    suspend fun updateTransactionStatus(
        transactionId: String,
        status: TransactionStatus
    ): Result<Unit>

    suspend fun deleteTransaction(transactionId: String): Result<Unit>

    suspend fun restoreTransaction(transaction: Transaction): Result<Unit>

}