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
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    val firstNameInput = MutableLiveData<String>()
    val emailInput = MutableLiveData<String>()
    val phoneInput = MutableLiveData<String>()

    private val _signUpResult = MutableLiveData<Boolean>()
    val signUpResult: LiveData<Boolean> get() = _signUpResult

    private val _errorMessage = MutableLiveData<String>()
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

                // Kart yaradılır
                createUniqueCardForUser(uid)

                // FCM token
                fetchFcmToken(uid)

                _signUpResult.value = true

            } catch (e: Exception) {
                _signUpResult.value = false
                _errorMessage.value = e.message
            }
        }
    }

    private suspend fun createUniqueCardForUser(uid: String) {
        val cardsCollection = firestore.collection("users").document(uid).collection("cards")
        var uniqueCardNumber: String

        do {
            uniqueCardNumber = generateCardNumber()
            val querySnapshot = cardsCollection.whereEqualTo("cardNumber", uniqueCardNumber).get().await()
        } while (!querySnapshot.isEmpty)

        val cvv = (100..999).random().toString()
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100
        val expiryYear = (currentYear + (3..5).random()).toString().padStart(2, '0')
        val expiryMonth = (1..12).random().toString().padStart(2, '0')
        val expiryDate = "$expiryMonth/$expiryYear"

        val cardId = cardsCollection.document().id

        val cardMap = hashMapOf(
            "cardId" to cardId,
            "cardNumber" to uniqueCardNumber,
            "cvv" to cvv,
            "expiryDate" to expiryDate,
            "balance" to 0.0,
            "currency" to "AZN",
            "createdAt" to FieldValue.serverTimestamp()
        )

        cardsCollection.document(cardId).set(cardMap).await()
    }

    private fun generateCardNumber(): String {
        val prefix = "4169"
        return "$prefix ${(1000..9999).random()} ${(1000..9999).random()} ${(1000..9999).random()}"
    }

    private fun fetchFcmToken(uid: String) {
        FierbaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                firestore.collection("users").document(uid)
                    .update("fcmToken", token)
            }
        }
    }
}


