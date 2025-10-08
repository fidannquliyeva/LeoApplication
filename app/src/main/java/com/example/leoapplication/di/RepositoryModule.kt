package com.example.leoapplication.di

import com.example.leoapplication.data.repository.PaymentRepositoryImpl
import com.example.leoapplication.data.repository.AuthRepositoryImpl
import com.example.leoapplication.data.repository.CardRepositoryImpl
import com.example.leoapplication.data.repository.TransactionRepositoryImpl
import com.example.leoapplication.domain.repository.AuthRepository
import com.example.leoapplication.domain.repository.CardRepository
import com.example.leoapplication.domain.repository.PaymentRepository
import com.example.leoapplication.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCardRepository(
        cardRepositoryImpl: CardRepositoryImpl
    ): CardRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl
    ): PaymentRepository
}