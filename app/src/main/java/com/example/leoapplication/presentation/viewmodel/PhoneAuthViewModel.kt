package com.example.leoapplication.presentation.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _otpSent = MutableLiveData<String>() // verificationId
    val otpSent: LiveData<String> get() = _otpSent

    private val _otpVerified = MutableLiveData<Boolean>()
    val otpVerified: LiveData<Boolean> get() = _otpVerified

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _otpAutoFilled = MutableLiveData<String>()
    val otpAutoFilled: LiveData<String> get() = _otpAutoFilled


    // Firestore-da nömrəni yoxla və OTP göndər
    fun checkNumberAndSendOtp(phoneNumber: String, activity: Activity) {
        firestore.collection("users")
            .whereEqualTo("phone", phoneNumber)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    startPhoneVerification(phoneNumber, activity)
                } else {
                    _errorMessage.value = "Bu nömrə ilə qeydiyyat yoxdur"
                }
            }
            .addOnFailureListener { e ->
                _errorMessage.value = e.message
            }
    }

    // OTP göndərmə
    private fun startPhoneVerification(phoneNumber: String, activity: Activity) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // SMS kodu varsa TextView-a avtomatik yaz
                    credential.smsCode?.let { code ->
                        _otpAutoFilled.value = code
                    }
                    // Avtomatik təsdiqləndi
                    _otpVerified.value = true
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _errorMessage.value = e.message
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    _otpSent.value = verificationId
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // OTP yoxlama
    fun verifyOtp(verificationId: String, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _otpVerified.value = task.isSuccessful
                if (!task.isSuccessful) _errorMessage.value = task.exception?.message
            }
    }
}
