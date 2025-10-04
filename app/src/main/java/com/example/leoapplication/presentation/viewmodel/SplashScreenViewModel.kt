package com.example.leoapplication.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _navigateToMain = MutableLiveData<Boolean>()
    val navigateToMain: LiveData<Boolean> get() = _navigateToMain

    private val _navigateToSignUp = MutableLiveData<Boolean>()
    val navigateToSignUp: LiveData<Boolean> get() = _navigateToSignUp
//
//    fun checkUser() {
//        val currentUser = FirebaseAuth.getInstance().currentUser
//        if (currentUser != null) {
//            // Kart yoxlanışı
//            firestore.collection("cards")
//                .document(currentUser.uid)
//                .get()
//                .addOnSuccessListener { doc ->
//                    if (doc.exists()) {
//                        _navigateToMain.value = true
//                    } else {
//                        _navigateToSignUp.value = true
//                    }
//                }
//                .addOnFailureListener {
//                    _navigateToSignUp.value = true
//                }
//        } else {
//            _navigateToSignUp.value = true
//        }
//    }
}
