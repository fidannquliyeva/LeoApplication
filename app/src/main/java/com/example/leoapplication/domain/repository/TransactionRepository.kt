package com.example.leoapplication.domain.repository

import com.example.leoapplication.data.model.Transaction
import com.example.leoapplication.data.model.TransactionStatus


interface TransactionRepository {

    /**
     * Pul köçürmək
     */
    suspend fun transferMoney(
        fromUserId: String,
        toPhoneNumber: String,
        amount: Double,
        description: String
    ): Result<Transaction>

    /**
     * İstifadəçinin transaksiyalarını almaq
     */
    suspend fun getUserTransactions(userId: String): Result<List<Transaction>>

    /**
     * Transaksiya statusunu yeniləmək
     */
    suspend fun updateTransactionStatus(
        transactionId: String,
        status: TransactionStatus
    ): Result<Unit>
}