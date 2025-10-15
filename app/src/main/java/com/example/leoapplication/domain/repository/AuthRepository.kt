package com.example.leoapplication.domain.repository

import com.example.leoapplication.data.model.Card
import com.example.leoapplication.data.model.User
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider


interface AuthRepository {

    fun sendVerificationCode(
        phoneNumber: String,
        activity: androidx.fragment.app.FragmentActivity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )

    suspend fun verifyPhoneNumber(credential: PhoneAuthCredential): Result<String>

    suspend fun createUserProfile(user: User): Result<Unit>

    suspend fun createInitialCard(userId: String, phoneNumber: String): Result<Card>


    suspend fun getUserByPhoneNumber(phoneNumber: String): Result<User?>

    fun getCurrentUserId(): String?

    fun isUserLoggedIn(): Boolean

    fun signOut()
}