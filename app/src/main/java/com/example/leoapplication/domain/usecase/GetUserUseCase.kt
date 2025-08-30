package com.example.leoapplication.domain.usecase

import com.example.leoapplication.domain.model.User
import com.example.leoapplication.domain.repository.UserRepository

class GetUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(phone: String) = repository.getUserByPhone(phone)
}

class CreateUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(user: User) = repository.createUser(user)
}

class AddCardToUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(phone: String, cardNumber: String) = repository.addCardToUser(phone, cardNumber)
}
