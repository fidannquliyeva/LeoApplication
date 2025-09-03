package com.example.leoapplication.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.data.repository.UserRepositoryImpl
import com.example.leoapplication.domain.model.User
import com.example.leoapplication.domain.repository.UserRepository
import com.example.leoapplication.domain.usecase.GetUserUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ProfileVM @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val repository: UserRepository
) : ViewModel() {

    val userProfile = MutableLiveData<User?>()
    val error = MutableLiveData<String?>()

    fun loadUserProfile(uid: String) {
        viewModelScope.launch {
            try {
                val user = repository.getUserByUid(uid)  // artıq repository mövcuddur
                userProfile.value = user
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }
}
