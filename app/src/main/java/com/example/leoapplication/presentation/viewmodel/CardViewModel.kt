package com.example.leoapplication.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.data.model.Card
import com.example.leoapplication.domain.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CardUiState>(CardUiState.Loading)
    val uiState: StateFlow<CardUiState> = _uiState.asStateFlow()

    private val _card = MutableStateFlow<Card?>(null)
    val card: StateFlow<Card?> = _card.asStateFlow()

    private val _isBlocked = MutableStateFlow(false)
    val isBlocked: StateFlow<Boolean> = _isBlocked.asStateFlow()

    fun loadCard(cardId: String) {
        viewModelScope.launch {
            _uiState.value = CardUiState.Loading

            val result = cardRepository.getCardById(cardId)

            if (result.isSuccess) {
                val cardData = result.getOrNull()
                if (cardData != null) {
                    _card.value = cardData
                    _isBlocked.value = !cardData.isActive
                    _uiState.value = CardUiState.Success
                }
                else {
                    _uiState.value = CardUiState.Error("Kart tapılmadı")
                }
            } else {
                val error = result.exceptionOrNull()?.message ?: "Xəta baş verdi"
                _uiState.value = CardUiState.Error(error)
            }
        }
    }

    fun toggleCardBlock() {
        viewModelScope.launch {
            val currentCard = _card.value ?: return@launch

            val currentIsBlocked = _isBlocked.value
            val newIsActive = currentIsBlocked  // Əgər blokludursa, aktivləşdir
            val newIsBlocked = !newIsActive     // Əgər aktivdirsə, blokla


            val result = cardRepository.toggleCardStatus(currentCard.cardId, newIsActive)

            if (result.isSuccess) {
                _isBlocked.value = newIsBlocked
                _card.value = currentCard.copy(isActive = newIsActive)

                val message = if (newIsActive) {
                    "✅ Kart aktivləşdirildi"
                } else {
                    "⚠️ Kart bloklandı"
                }

                _uiState.value = CardUiState.Message(message)
            } else {
                _uiState.value = CardUiState.Error("Əməliyyat uğursuz oldu")
            }
        }
    }
}

sealed class CardUiState {
    object Loading : CardUiState()
    object Success : CardUiState()
    data class Error(val message: String?) : CardUiState()
    data class Message(val message: String) : CardUiState()
}