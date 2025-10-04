package com.example.leoapplication.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.domain.model.Card
import com.example.leoapplication.domain.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    val firstNameInput = MutableLiveData<String>()
    val emailInput = MutableLiveData<String>()
    val phoneInput = MutableLiveData<String>()

    private val _signUpResult = MutableLiveData<Boolean>()
    val signUpResult: LiveData<Boolean> get() = _signUpResult

    private val _errorMessage = MutableLiveData<String>()
    private var currentUserUid: String? = null

    val errorMessage: LiveData<String> get() = _errorMessage

    fun signUp() {
        val firstName = firstNameInput.value?.trim()
        val email = emailInput.value?.trim()
        val phone = phoneInput.value?.trim()

        if (firstName.isNullOrEmpty() || email.isNullOrEmpty() || phone.isNullOrEmpty()) {
            _errorMessage.value = "Bütün sahələri doldurun"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Düzgün email daxil edin"
            return
        }

        viewModelScope.launch {
            try {
                val uid = firestore.collection("users").document().id

                val userMap = hashMapOf(
                    "uid" to uid,
                    "firstName" to firstName,
                    "email" to email,
                    "phone" to phone,
                    "createdAt" to FieldValue.serverTimestamp()
                )

                firestore.collection("users").document(uid).set(userMap).await()

                // FCM token
                fetchFcmToken(uid)

                _signUpResult.value = true

            } catch (e: Exception) {
                _signUpResult.value = false
                _errorMessage.value = e.message
            }
        }
    }

    fun getCurrentUserUid(): String {
        return currentUserUid ?: throw IllegalStateException("User UID is null")
    }

    private fun fetchFcmToken(uid: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                firestore.collection("users").document(uid)
                    .update("fcmToken", token)
            }
        }
    }
}
