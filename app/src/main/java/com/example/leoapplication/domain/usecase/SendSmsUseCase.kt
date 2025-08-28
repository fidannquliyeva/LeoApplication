package com.example.leoapplication.domain.usecase

import android.app.Activity
import com.example.leoapplication.domain.repository.AuthRepository
import javax.inject.Inject

class SendSmsUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(phone: String, activity: Activity) {
        repository.sendSms(phone, activity)
    }
}