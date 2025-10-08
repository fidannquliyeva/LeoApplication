package com.example.leoapplication.domain.usecase.auth

import com.example.leoapplication.data.model.Card
import com.example.leoapplication.data.model.User
import com.example.leoapplication.domain.repository.AuthRepository
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(user: User): Result<Card> {
        return try {
            // 1. User profili yarat
            val userResult = repository.createUserProfile(user)
            if (userResult.isFailure) {
                return Result.failure(
                    userResult.exceptionOrNull() ?: Exception("User yaradılmadı")
                )
            }

            // 2. İlkin kartı yarat
            val cardResult = repository.createInitialCard(user.userId, user.phoneNumber)
            if (cardResult.isFailure) {
                return Result.failure(
                    cardResult.exceptionOrNull() ?: Exception("Kart yaradılmadı")
                )
            }

            cardResult
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}