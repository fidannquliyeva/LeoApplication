package com.example.leoapplication.domain.usecase

import com.example.leoapplication.domain.repository.AuthRepository
import javax.inject.Inject


class VerifyCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(verificationId: String, code: String): Boolean {
        return repository.verifyCode(verificationId, code)
    }
}