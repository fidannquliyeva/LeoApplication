package com.example.leoapplication.domain.repository

import com.example.leoapplication.data.model.Card
import com.example.leoapplication.data.model.User
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider


interface AuthRepository {

    /**
     * OTP göndərmə
     */
    fun sendVerificationCode(
        phoneNumber: String,
        activity: androidx.fragment.app.FragmentActivity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )

    /**
     * OTP təsdiqləmə
     */
    suspend fun verifyPhoneNumber(credential: PhoneAuthCredential): Result<String>

    /**
     * İstifadəçi profili yaratmaq
     */
    suspend fun createUserProfile(user: User): Result<Unit>

    /**
     * İlkin kart yaratmaq
     */
    suspend fun createInitialCard(userId: String, phoneNumber: String): Result<Card>

    /**
     * Telefon nömrəsi ilə istifadəçi tapmaq
     */
    suspend fun getUserByPhoneNumber(phoneNumber: String): Result<User?>

    /**
     * Hazırki istifadəçi UID
     */
    fun getCurrentUserId(): String?

    /**
     * İstifadəçi giriş edib?
     */
    fun isUserLoggedIn(): Boolean

    /**
     * Çıxış
     */
    fun signOut()
}