package com.example.leoapplication.domain.repository

import android.app.Activity


interface AuthRepository {
    suspend fun sendSms(phone: String, activity: Activity)
    suspend fun verifyCode(verificationId: String, code: String): Boolean
}
