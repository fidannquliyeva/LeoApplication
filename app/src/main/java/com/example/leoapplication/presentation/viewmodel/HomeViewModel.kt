package com.example.leoapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.domain.repository.HomeRepository
import com.example.leoapplication.domain.repository.TransactionRepository
import com.example.leoapplication.data.model.Card
import com.example.leoapplication.data.model.Transaction
import com.example.leoapplication.data.model.User
import com.example.leoapplication.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val transactionRepository: TransactionRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData.asStateFlow()

    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards: StateFlow<List<Card>> = _cards.asStateFlow()

    private val _selectedCard = MutableStateFlow<Card?>(null)
    val selectedCard: StateFlow<Card?> = _selectedCard.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _filteredTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val filteredTransactions: StateFlow<List<Transaction>> = _filteredTransactions.asStateFlow()

    init {
        loadUserData()
        observeCards()
        loadTransactions()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            when (val result = homeRepository.getUserData()) {
                is Resource.Success -> {
                    _userData.value = result.data
                }
                is Resource.Error -> {
                    _uiState.value = HomeUiState.Error(result.message)
                }
                is Resource.Loading -> {
                    _uiState.value = HomeUiState.Loading
                }
            }
        }
    }

    private fun observeCards() {
        viewModelScope.launch {
            homeRepository.observeUserCards().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _cards.value = result.data ?: emptyList()

                        if (_selectedCard.value == null && result.data?.isNotEmpty() == true) {
                            _selectedCard.value = result.data.first()
                        }

                        _uiState.value = HomeUiState.Success
                    }
                    is Resource.Error -> {
                        _uiState.value = HomeUiState.Error(result.message)
                    }
                    is Resource.Loading -> {
                        _uiState.value = HomeUiState.Loading
                    }
                }
            }
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            // ✅ DÜZGÜN YOL
            val result = transactionRepository.getUserTransactions(userId)

            result.onSuccess { txList ->
                _transactions.value = txList
                _filteredTransactions.value = txList
            }

            result.onFailure { error ->
                _transactions.value = emptyList()
                _filteredTransactions.value = emptyList()
                // İstəsən error message göstər:
                // _uiState.value = HomeUiState.Error(error.message ?: "Transaksiyalar yüklənmədi")
            }
        }
    }

    fun searchTransactions(query: String) {
        if (query.isBlank()) {
            _filteredTransactions.value = _transactions.value
        } else {
            _filteredTransactions.value = _transactions.value.filter {
                it.description.contains(query, ignoreCase = true) ||
                        it.amount.toString().contains(query)
            }
        }
    }

    fun selectCard(card: Card) {
        _selectedCard.value = card
    }

    fun refresh() {
        loadUserData()
        loadTransactions()
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Success : HomeUiState()
    data class Error(val message: String?) : HomeUiState()
}