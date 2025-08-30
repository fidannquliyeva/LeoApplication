package com.example.leoapplication.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.domain.model.BankCard
import com.example.leoapplication.domain.model.User
import com.example.leoapplication.domain.repository.UserRepository
import com.example.leoapplication.domain.usecase.AddCardToUserUseCase
import com.example.leoapplication.domain.usecase.CreateCardUseCase
import com.example.leoapplication.domain.usecase.CreateUserUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewUserInfoVM @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val createCardUseCase: CreateCardUseCase,
    private val addCardToUserUseCase: AddCardToUserUseCase
) : ViewModel() {

    val registrationSuccess = MutableLiveData<Boolean>()
    val error = MutableLiveData<String?>()
    val bankCard = MutableLiveData<BankCard?>() // ← əlavə edildi

    fun registerUser(fullName: String, email: String, phone: String) {
        viewModelScope.launch {
            try {
                val cleanPhone = phone.replace("+994", "").replace(" ", "")
                val newUser = User(fullName = fullName, email = email, phone = cleanPhone)
                createUserUseCase(newUser)

                val newCard = BankCard(
                    cardNumber = (1..16).map { (0..9).random() }.joinToString(""),
                    cvv = (100..999).random().toString(),
                    mm = (1..12).random().toString().padStart(2, '0'),
                    yy = ((23..30).random()).toString(),
                    balance = 0.0,
                    ownerPhone = cleanPhone, // telefon nömrəsi ilə bağlandı
                    isNewCard = true
                )

                createCardUseCase(newCard)
                addCardToUserUseCase(cleanPhone, newCard.cardNumber)

                bankCard.value = newCard // ← burda LiveData doldurulur
                registrationSuccess.value = true
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }
}
