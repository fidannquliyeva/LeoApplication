package com.example.leoapplication.domain.usecase.card

import com.example.leoapplication.data.model.Card
import com.example.leoapplication.domain.repository.CardRepository
import javax.inject.Inject

class GetUserCardsUseCase @Inject constructor(
    private val repository: CardRepository
) {
    suspend operator fun invoke(userId: String): Result<List<Card>> {
        return repository.getUserCards(userId)
    }
}