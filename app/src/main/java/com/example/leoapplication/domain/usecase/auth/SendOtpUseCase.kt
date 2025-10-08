package com.example.leoapplication.domain.usecase.auth

import com.example.leoapplication.domain.repository.AuthRepository
import com.google.firebase.auth.PhoneAuthProvider
import javax.inject.Inject

class SendOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(
        phoneNumber: String,
        activity: androidx.fragment.app.FragmentActivity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        repository.sendVerificationCode(phoneNumber, activity, callbacks)
    }
}