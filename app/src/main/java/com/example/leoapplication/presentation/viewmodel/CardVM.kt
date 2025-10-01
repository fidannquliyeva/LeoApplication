package com.example.leoapplication.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.leoapplication.domain.model.Card
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CardVM @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    val bankCard = MutableLiveData<Card?>()
    val error = MutableLiveData<String?>()

    fun fetchCardByPhone(phoneNumber: String) {
        val cleanPhone = phoneNumber.replace("+994", "").replace(" ", "")

        db.collection("bankCards")
            .whereEqualTo("ownerPhone", cleanPhone)
            .get()
            .addOnSuccessListener { snapshot: QuerySnapshot ->
                if (!snapshot.isEmpty) {
                    val card = snapshot.documents[0].toObject(Card::class.java)
                    bankCard.value = card
                } else {
                    error.value = "Kart tapılmadı"
                }
            }
            .addOnFailureListener { e ->
                error.value = e.message
            }
    }
}
