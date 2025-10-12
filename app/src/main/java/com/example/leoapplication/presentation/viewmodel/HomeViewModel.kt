package com.example.leoapplication.presentation.viewmodel

import android.util.Log
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
        val currentUser = auth.currentUser
        Log.d("HomeViewModel", "====== INIT ======")
        Log.d("HomeViewModel", "Current user: ${currentUser?.uid}")

        if (currentUser == null) {
            Log.e("HomeViewModel", "❌ User not logged in!")
            _uiState.value = HomeUiState.Error("İstifadəçi daxil olmayıb")
        }

        loadUserData()
        observeCards()
        observeTransactions() // ✅ Real-time observer
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
            Log.d("HomeViewModel", "Starting to observe cards...")
            homeRepository.observeUserCards().collect { result ->
                Log.d("HomeViewModel", "Cards result: $result")

                when (result) {
                    is Resource.Success -> {
                        val cards = result.data ?: emptyList()
                        _cards.value = cards

                        Log.d("HomeViewModel", "✅ Cards loaded: ${cards.size}")
                        cards.forEach { card ->
                            Log.d(
                                "HomeViewModel",
                                "  - ${card.cardNumber}: ${card.balance} ${card.currency}"
                            )
                        }

                        if (cards.isNotEmpty()) {
                            val currentSelectedCardId = _selectedCard.value?.cardId

                            if (currentSelectedCardId != null) {
                                val updatedCard = cards.find { it.cardId == currentSelectedCardId }
                                if (updatedCard != null) {
                                    _selectedCard.value = updatedCard
                                    Log.d(
                                        "HomeViewModel",
                                        "✅ Selected card updated: ${updatedCard.cardNumber} - Balance: ${updatedCard.balance}"
                                    )
                                } else {
                                    _selectedCard.value = cards.first()
                                    Log.d(
                                        "HomeViewModel",
                                        "⚠️ Selected card not found, switching to first card"
                                    )
                                }
                            } else {
                                _selectedCard.value = cards.first()
                                Log.d(
                                    "HomeViewModel",
                                    "✅ Selected first card initially: ${cards.first().cardNumber}"
                                )
                            }
                        } else {
                            _selectedCard.value = null
                            Log.d("HomeViewModel", "❌ No cards available")
                        }

                        _uiState.value = HomeUiState.Success
                    }
                    is Resource.Error -> {
                        Log.e("HomeViewModel", "❌ Cards error: ${result.message}")
                        _uiState.value = HomeUiState.Error(result.message)
                    }
                    is Resource.Loading -> {
                        Log.d("HomeViewModel", "⏳ Loading cards...")
                        _uiState.value = HomeUiState.Loading
                    }
                }
            }
        }
    }

    // ✅ YENİ - Real-time transaction observer
    private fun observeTransactions() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                Log.e("HomeViewModel", "❌ Cannot observe transactions: user is null")
                return@launch
            }

            Log.d("HomeViewModel", "Starting to observe transactions...")

            transactionRepository.observeUserTransactions(userId).collect { result ->
                result.onSuccess { txList ->
                    _transactions.value = txList
                    _filteredTransactions.value = txList
                    Log.d("HomeViewModel", "✅ Transactions updated: ${txList.size}")
                }

                result.onFailure { error ->
                    _transactions.value = emptyList()
                    _filteredTransactions.value = emptyList()
                    Log.e("HomeViewModel", "❌ Transactions error: ${error.message}")
                }
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
        Log.d("HomeViewModel", "Card selected manually: ${card.cardNumber}")
    }

    fun refresh() {
        Log.d("HomeViewModel", "====== REFRESHING DATA ======")
        loadUserData()
        // ❌ loadTransactions() - artıq lazım deyil, real-time observer var!
    }

    // ✅ Transaction sil
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Deleting transaction: ${transaction.transactionId}")

                val result = transactionRepository.deleteTransaction(transaction.transactionId)

                if (result.isSuccess) {
                    Log.d("HomeViewModel", "✅ Transaction deleted")
                    // ❌ Local state yeniləməyə ehtiyac yoxdur - observer avtomatik yeniləyir!
                } else {
                    Log.e("HomeViewModel", "❌ Delete failed: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ Delete failed: ${e.message}")
            }
        }
    }

    // ✅ Transaction geri qaytır (Undo)
    fun restoreTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Restoring transaction: ${transaction.transactionId}")

                val result = transactionRepository.restoreTransaction(transaction)

                if (result.isSuccess) {
                    Log.d("HomeViewModel", "✅ Transaction restored successfully")
                    // ❌ Local state yeniləməyə ehtiyac yoxdur - observer avtomatik yeniləyir!
                } else {
                    Log.e("HomeViewModel", "❌ Restore failed: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ Restore failed: ${e.message}")
            }
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Success : HomeUiState()
    data class Error(val message: String?) : HomeUiState()
}