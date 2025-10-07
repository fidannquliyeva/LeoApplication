package com.example.leoapplication.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {

    /**
     * OTP göndərmə
     */
    fun sendVerificationCode(
        phoneNumber: String,
        activity: androidx.fragment.app.FragmentActivity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /**
     * OTP təsdiqləmə
     */
    suspend fun verifyPhoneNumber(credential: PhoneAuthCredential): Result<String> {
        return try {
            val result = auth.signInWithCredential(credential).await()
            val uid = result.user?.uid
                ?: throw Exception("User ID alınmadı")
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Hazırki istifadəçi UID
     */
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    /**
     * İstifadəçi qeydiyyatdan keçib?
     */
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    /**
     * Çıxış
     */
    fun signOut() = auth.signOut()
}