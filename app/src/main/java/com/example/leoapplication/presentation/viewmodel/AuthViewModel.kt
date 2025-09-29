package com.example.leoapplication.presentation.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    val firstNameInput = MutableLiveData<String>()

    val emailInput = MutableLiveData<String>()
    val phoneInput = MutableLiveData<String>()
    val passwordInput = MutableLiveData<String>()
    val confirmPasswordInput = MutableLiveData<String>()

    private val _signUpResult = MutableLiveData<Boolean>()
    val signUpResult: LiveData<Boolean> get() = _signUpResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun signUp() {
        val firstName = firstNameInput.value?.trim()
        val email = emailInput.value?.trim()
        val phone = phoneInput.value?.trim()
        val password = passwordInput.value
        val confirmPassword = confirmPasswordInput.value

        // Validation
        if (firstName.isNullOrEmpty() || email.isNullOrEmpty() || phone.isNullOrEmpty() ||
            password.isNullOrEmpty() || confirmPassword.isNullOrEmpty()
        ) {
            _errorMessage.value = "Bütün sahələri doldurun"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Düzgün email daxil edin"
            return
        }

        if (password != confirmPassword) {
            _errorMessage.value = "Şifrə və təsdiq uyğun deyil"
            return
        }

        if (password.length < 8) {
            _errorMessage.value = "Şifrə ən azı 8 simvol olmalıdır"
            return
        }

        // Firebase signup
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val userData = hashMapOf(
                        "uid" to uid,
                        "firstName" to firstName,
                        "email" to email,
                        "phone" to phone,
                        "createdAt" to FieldValue.serverTimestamp()
                    )

                    firestore.collection("users").document(uid)
                        .set(userData)
                        .addOnSuccessListener {
                            _signUpResult.value = true
                            fetchFcmToken(uid)
                        }
                        .addOnFailureListener { e ->
                            _signUpResult.value = false
                            _errorMessage.value = e.message
                        }

                } else {
                    _signUpResult.value = false
                    _errorMessage.value = task.exception?.message
                }
            }
    }

    private fun fetchFcmToken(uid: String) {
        // FCM token alma və Firestore-da qeyd etmək
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                firestore.collection("users").document(uid)
                    .update("fcmToken", token)
            }
        }
    }
}
