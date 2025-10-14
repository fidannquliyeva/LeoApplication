package com.example.leoapplication.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.domain.repository.HomeRepository
import com.example.leoapplication.domain.repository.TransactionRepository
import com.example.leoapplication.data.model.Card
import com.example.leoapplication.data.model.Transaction
import com.example.leoapplication.data.model.TransactionType
import com.example.leoapplication.data.model.User
import com.example.leoapplication.util.NotificationHelper
import com.example.leoapplication.util.Resource
import com.example.leoapplication.util.TransactionSearchHelper
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val transactionRepository: TransactionRepository,
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData.asStateFlow()

    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards: StateFlow<List<Card>> = _cards.asStateFlow()

    private val _selectedCard = MutableStateFlow<Card?>(null)
    val selectedCard: StateFlow<Card?> = _selectedCard.asStateFlow()

    // ✅ YENİ: Balance StateFlow
    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _filteredTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val filteredTransactions: StateFlow<List<Transaction>> = _filteredTransactions.asStateFlow()

    private var oldTransactionIds = emptySet<String>()
    private var isFirstLoad = true

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
        observeTransactions()
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
                            Log.d("HomeViewModel", "  - ${card.cardNumber}: ${card.balance} ${card.currency}")
                        }

                        if (cards.isNotEmpty()) {
                            val currentSelectedCardId = _selectedCard.value?.cardId

                            if (currentSelectedCardId != null) {
                                val updatedCard = cards.find { it.cardId == currentSelectedCardId }
                                if (updatedCard != null) {
                                    _selectedCard.value = updatedCard
                                    // ✅ Balansı yenilə
                                    _balance.value = updatedCard.balance
                                    Log.d("HomeViewModel", "✅ Selected card updated: ${updatedCard.cardNumber} - Balance: ${updatedCard.balance}")
                                } else {
                                    _selectedCard.value = cards.first()
                                    _balance.value = cards.first().balance
                                    Log.d("HomeViewModel", "⚠️ Selected card not found, switching to first card")
                                }
                            } else {
                                _selectedCard.value = cards.first()
                                _balance.value = cards.first().balance
                                Log.d("HomeViewModel", "✅ Selected first card initially: ${cards.first().cardNumber}")
                            }
                        } else {
                            _selectedCard.value = null
                            _balance.value = 0.0
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

                    if (!isFirstLoad && oldTransactionIds.isNotEmpty()) {
                        val currentTime = System.currentTimeMillis()
                        val tenSecondsAgo = currentTime - 10_000

                        val newTransactions = txList.filter { tx ->
                            tx.transactionId !in oldTransactionIds &&
                                    tx.timestamp >= tenSecondsAgo
                        }

                        Log.d("HomeViewModel", "🔔 New transactions (last 10 sec): ${newTransactions.size}")

                        newTransactions.forEach { tx ->
                            showNotificationForTransaction(tx, userId)
                        }
                    }

                    oldTransactionIds = txList.map { it.transactionId }.toSet()
                    isFirstLoad = false
                }

                result.onFailure { error ->
                    _transactions.value = emptyList()
                    _filteredTransactions.value = emptyList()
                    Log.e("HomeViewModel", "❌ Transactions error: ${error.message}")
                }
            }
        }
    }

    private fun showNotificationForTransaction(transaction: Transaction, currentUserId: String) {
        Log.d("HomeViewModel", "🔔 Showing notification for: ${transaction.type}")

        when (transaction.type) {
            TransactionType.BALANCE_INCREASE -> {
                NotificationHelper.showBalanceIncreaseNotification(
                    context,
                    transaction.amount
                )
            }

            TransactionType.TRANSFER -> {
                val isReceived = transaction.toUserId == currentUserId

                if (isReceived) {
                    NotificationHelper.showTransactionReceivedNotification(
                        context,
                        transaction.amount,
                        transaction.description
                    )
                } else {
                    NotificationHelper.showTransactionSentNotification(
                        context,
                        transaction.amount,
                        transaction.description
                    )
                }
            }

            else -> {
                Log.d("HomeViewModel", "Other transaction type: ${transaction.type}")
            }
        }
    }

    fun searchTransactions(query: String) {
        Log.d("HomeViewModel", "🔍 Searching: '$query'")

        if (query.isBlank()) {
            _filteredTransactions.value = _transactions.value
            Log.d("HomeViewModel", "📋 Showing all ${_transactions.value.size} transactions")
            return
        }

        val userId = auth.currentUser?.uid ?: ""

        val filtered = _transactions.value.filter { transaction ->
            TransactionSearchHelper.matchesQuery(transaction, query, userId)
        }

        _filteredTransactions.value = filtered

        Log.d("HomeViewModel", "✅ Found ${filtered.size} of ${_transactions.value.size} transactions")

        if (filtered.isNotEmpty()) {
            filtered.forEach { tx ->
                Log.d("HomeViewModel", "  → [${tx.type}] ${tx.amount} ${tx.currency} - ${tx.description}")
            }
        }
    }


    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Deleting transaction: ${transaction.transactionId}")

                val result = transactionRepository.deleteTransaction(transaction.transactionId)

                if (result.isSuccess) {
                    Log.d("HomeViewModel", "✅ Transaction deleted")
                } else {
                    Log.e("HomeViewModel", "❌ Delete failed: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ Delete failed: ${e.message}")
            }
        }
    }

    fun restoreTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Restoring transaction: ${transaction.transactionId}")

                val result = transactionRepository.restoreTransaction(transaction)

                if (result.isSuccess) {
                    Log.d("HomeViewModel", "✅ Transaction restored successfully")
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