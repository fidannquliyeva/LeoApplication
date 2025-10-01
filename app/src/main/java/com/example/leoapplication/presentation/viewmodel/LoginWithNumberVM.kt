package com.example.leoapplication.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.domain.model.Card
import com.example.leoapplication.domain.usecase.CreateCardUseCase
import com.example.leoapplication.domain.usecase.GetUserUseCase
import com.example.leoapplication.domain.usecase.GetCardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginWithNumberVM @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getCardUseCase: GetCardUseCase,
    private val createCardUseCase: CreateCardUseCase
) : ViewModel() {

    var phoneNumber: String = ""
    val navigateToNewUser = MutableLiveData<Boolean>()
    val navigateToSmsLogin = MutableLiveData<Boolean>()
    val error = MutableLiveData<String?>()
    val bankCard = MutableLiveData<Card?>()

    fun fetchUserOrRedirect() {
        viewModelScope.launch {
            try {
                val cleanPhone = phoneNumber.replace("+994", "").replace(" ", "")
                val user = getUserUseCase(cleanPhone)

                if (user == null) {
                    navigateToNewUser.value = true
                } else {
                    val card = getCardUseCase(cleanPhone)
                    if (card != null) bankCard.value = card
                    navigateToSmsLogin.value = true
                }
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }
}
