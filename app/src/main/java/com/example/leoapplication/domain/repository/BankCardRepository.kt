package com.example.leoapplication.domain.repository

import com.example.leoapplication.domain.model.BankCard


interface BankCardRepository {
    suspend fun getCardByPhone(phone: String): BankCard?
    suspend fun createCard(card: BankCard)
}
