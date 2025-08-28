package com.example.leoapplication.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.domain.model.BankCard
import com.example.leoapplication.domain.usecase.CreateCardUseCase
import com.example.leoapplication.domain.usecase.GetCardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginWithNumberVM @Inject constructor(
    private val getCardUseCase: GetCardUseCase,
    private val createCardUseCase: CreateCardUseCase
) : ViewModel() {

    var phoneNumber: String = ""

    private val _bankCard = MutableLiveData<BankCard?>()
    val bankCard: LiveData<BankCard?> = _bankCard

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error


    fun fetchOrCreateCard() {
        viewModelScope.launch {
            try {
                val cleanPhone = phoneNumber.replace("+994", "").replace(" ", "")

                // Firebase-də nömrə yoxlanır
                val card = getCardUseCase(cleanPhone) // burada Firebase-dən fetch edilir
                if (card != null) {
                    // Kart tapıldı → SMS səhifəsinə yönləndir
                    _bankCard.value = card
                } else {
                    // Kart yoxdursa → yeni kart yaradılır
                    val newCard = BankCard(
                        cardNumber = generateCardNumber(),
                        cvv = (100..999).random().toString(),
                        mm = (1..12).random().toString().padStart(2, '0'),
                        yy = ((23..30).random()).toString(),
                        balance = 0.0,
                        ownerPhone = cleanPhone,
                        isNewCard = true
                    )
                    createCardUseCase(newCard)
                    _bankCard.value = newCard
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Xəta baş verdi"
            }
        }
    }


    private fun generateCardNumber(): String {
        return (1..16).map { (0..9).random() }.joinToString("")
    }
}
