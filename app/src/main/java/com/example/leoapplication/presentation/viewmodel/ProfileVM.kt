package com.example.leoapplication.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.data.repository.UserRepositoryImpl
import com.example.leoapplication.domain.model.User
import com.example.leoapplication.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
class ProfileVM: ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _userData = MutableLiveData<Map<String, Any>>()
    val userData: LiveData<Map<String, Any>> = _userData

    fun loadUser(phone: String) {
        firestore.collection("users")
            .document(phone)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _userData.value = document.data
                } else {
                    _userData.value = emptyMap()
                }
            }
            .addOnFailureListener {
                _userData.value = emptyMap()
            }
    }
}
