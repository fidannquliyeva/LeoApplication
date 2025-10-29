package com.example.leoapplication.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.data.model.Card
import com.example.leoapplication.domain.repository.HomeRepository
import com.example.leoapplication.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncreaseBalanceViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _selectedCard = MutableStateFlow<Card?>(null)
    val selectedCard: StateFlow<Card?> = _selectedCard.asStateFlow()

    private val _uiState = MutableStateFlow<IncreaseBalanceUiState>(IncreaseBalanceUiState.Initial)
    val uiState: StateFlow<IncreaseBalanceUiState> = _uiState.asStateFlow()

    private val _amount = MutableStateFlow(0.0)
    val amount: StateFlow<Double> = _amount.asStateFlow()


    private val _externalCardNumber = MutableStateFlow("")
    val externalCardNumber: StateFlow<String> = _externalCardNumber.asStateFlow()

    init {
        loadUserCards()
    }

    private fun loadUserCards() {
        viewModelScope.launch {
            _uiState.value = IncreaseBalanceUiState.Loading

            homeRepository.observeUserCards().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val cards = result.data ?: emptyList()
                        if (cards.isNotEmpty()) {
                            _selectedCard.value = cards.first()
                            _uiState.value = IncreaseBalanceUiState.CardLoaded
                        } else {
                            _uiState.value = IncreaseBalanceUiState.Error("Kart tapılmadı")
                        }
                    }
                    is Resource.Error -> {
                        _uiState.value = IncreaseBalanceUiState.Error(result.message ?: "Xəta")
                    }
                    is Resource.Loading -> {
                        _uiState.value = IncreaseBalanceUiState.Loading
                    }
                }
            }
        }
    }

    fun setExternalCardNumber(cardNumber: String) {
        _externalCardNumber.value = cardNumber
    }

    fun increaseBalance(amount: Double, cardNumber: String) {
        val card = _selectedCard.value
        if (card == null) {
            _uiState.value = IncreaseBalanceUiState.Error("Kart seçilməyib")
            return
        }

        if (amount <= 0) {
            _uiState.value = IncreaseBalanceUiState.Error("Məbləğ 0-dan böyük olmalıdır")
            return
        }

        viewModelScope.launch {
            _uiState.value = IncreaseBalanceUiState.Loading

            val externalCardLast4 = cardNumber.replace(" ", "").takeLast(4)


            when (val result = homeRepository.increaseBalanceWithTransaction(
                card.cardId,
                amount,
                externalCardLast4
            )) {
                is Resource.Success -> {
                    _amount.value = amount
                    _uiState.value = IncreaseBalanceUiState.Success(amount)
                }
                is Resource.Error -> {
                    _uiState.value = IncreaseBalanceUiState.Error(
                        result.message ?: "Xəta baş verdi"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun resetState() {
        _uiState.value = IncreaseBalanceUiState.Initial
    }
}

sealed class IncreaseBalanceUiState {
    object Initial : IncreaseBalanceUiState()
    object Loading : IncreaseBalanceUiState()
    object CardLoaded : IncreaseBalanceUiState()
    data class Success(val amount: Double) : IncreaseBalanceUiState()
    data class Error(val message: String) : IncreaseBalanceUiState()
}