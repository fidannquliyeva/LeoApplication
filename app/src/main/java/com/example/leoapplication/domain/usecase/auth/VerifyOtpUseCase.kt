package com.example.leoapplication.domain.usecase.auth

import com.example.leoapplication.domain.repository.AuthRepository
import com.google.firebase.auth.PhoneAuthCredential
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(credential: PhoneAuthCredential): Result<String> {
        return repository.verifyPhoneNumber(credential)
    }
}