package com.example.leoapplication.data.repository

import android.app.Activity
import com.example.leoapplication.domain.repository.AuthRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    private var currentVerificationId: String? = null

    override suspend fun sendSms(phone: String, activity: Activity) = suspendCancellableCoroutine<Unit> { cont ->
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                cont.resume(Unit)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                cont.resumeWithException(e)
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                currentVerificationId = verificationId
                cont.resume(Unit)
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override suspend fun verifyCode(verificationId: String, code: String): Boolean = suspendCancellableCoroutine { cont ->
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) cont.resume(true)
                    else cont.resume(false)
                }
        } catch (e: Exception) {
            cont.resumeWithException(e)
        }
    }

    fun getCurrentVerificationId(): String? = currentVerificationId
}
