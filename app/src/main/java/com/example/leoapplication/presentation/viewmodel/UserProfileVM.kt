// presentation/viewmodel/UserProfileViewModel.kt
package com.example.leoapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.leoapplication.domain.model.User
import com.example.leoapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileVM @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    fun loadUser(phone: String) {
        viewModelScope.launch {
            val u = repository.getUserByPhone(phone)
            _user.value = u
        }
    }

    fun addCard(phone: String, cardNumber: String) {
        viewModelScope.launch {
            repository.addCardToUser(phone, cardNumber)
            loadUser(phone) // Yenilə
        }
    }
}
