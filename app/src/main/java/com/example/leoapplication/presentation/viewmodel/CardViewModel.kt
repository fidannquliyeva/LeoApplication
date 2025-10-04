package com.example.leoapplication.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.domain.model.Card
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _cardCreationResult = MutableLiveData<Boolean>()
    val cardCreationResult: LiveData<Boolean> get() = _cardCreationResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun createCardForUser(uid: String) {
        viewModelScope.launch {
            try {
                val cardsCollection = firestore.collection("users").document(uid).collection("cards")
                var uniqueCardNumber: String
                var querySnapshot: com.google.firebase.firestore.QuerySnapshot

                do {
                    uniqueCardNumber = generateCardNumber()
                    querySnapshot = cardsCollection.whereEqualTo("cardNumber", uniqueCardNumber).get().await()
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

                _cardCreationResult.value = true
            } catch (e: Exception) {
                _cardCreationResult.value = false
                _errorMessage.value = e.message
            }
        }
    }

    private fun generateCardNumber(): String {
        val prefix = "4169"
        return "$prefix ${(1000..9999).random()} ${(1000..9999).random()} ${(1000..9999).random()}"
    }
}
