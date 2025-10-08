package com.example.leoapplication.domain.usecase.card

import com.example.leoapplication.data.model.Card
import com.example.leoapplication.domain.repository.CardRepository
import javax.inject.Inject

class GetCardByIdUseCase @Inject constructor(
    private val repository: CardRepository
) {
    suspend operator fun invoke(cardId: String): Result<Card?> {
        return repository.getCardById(cardId)
    }
}