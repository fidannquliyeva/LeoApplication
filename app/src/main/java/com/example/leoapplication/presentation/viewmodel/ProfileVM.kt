package com.example.leoapplication.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.data.repository.UserRepositoryImpl
import com.example.leoapplication.domain.model.User
import com.example.leoapplication.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileVM @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val userLiveData = MutableLiveData<User?>()
    val logoutEvent = MutableLiveData<Boolean>()

    fun fetchUser(phone: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserByPhone(phone)
                userLiveData.value = user
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        logoutEvent.value = true
    }

//    // Avatarı dəyiş
//    fun updateAvatar(phone: String, avatarUri: Uri) {
//        viewModelScope.launch {
//            userRepository.updateUserAvatar(phone, avatarUri.toString())
//            // Yenidən istifadəçi məlumatını yüklə
//            fetchUser(phone)
//        }}
}