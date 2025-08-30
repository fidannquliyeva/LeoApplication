package com.example.leoapplication.domain.usecase

import com.example.leoapplication.domain.model.BankCard
import com.example.leoapplication.domain.repository.BankCardRepository

class GetCardUseCase(private val repository: BankCardRepository) {
    suspend operator fun invoke(phone: String) = repository.getCardByPhone(phone)
}

class CreateCardUseCase(private val repository: BankCardRepository) {
    suspend operator fun invoke(card: BankCard) = repository.createCard(card)
}