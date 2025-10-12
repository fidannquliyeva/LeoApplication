package com.example.leoapplication.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.data.model.ContactWithLeo
import com.example.leoapplication.data.model.Transaction
import com.example.leoapplication.data.model.TransactionType
import com.example.leoapplication.domain.repository.CardRepository
import com.example.leoapplication.domain.repository.TransactionRepository
import com.example.leoapplication.data.remote.FirestoreDataSource
import com.example.leoapplication.domain.usecase.transaction.TransferMoneyUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val transferMoneyUseCase: TransferMoneyUseCase,
    private val transactionRepository: TransactionRepository,
    private val cardRepository: CardRepository,
    private val firestoreDataSource: FirestoreDataSource,
    private val auth: FirebaseAuth
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow<TransferUiState>(TransferUiState.Idle)
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    // Current balance
    private val _currentBalance = MutableStateFlow(0.0)
    val currentBalance: StateFlow<Double> = _currentBalance.asStateFlow()

    // Recent contacts
    private val _recentContacts = MutableStateFlow<List<ContactWithLeo>>(emptyList())
    val recentContacts: StateFlow<List<ContactWithLeo>> = _recentContacts.asStateFlow()

    private val _filteredContacts = MutableStateFlow<List<ContactWithLeo>>(emptyList())
    val filteredContacts: StateFlow<List<ContactWithLeo>> = _filteredContacts.asStateFlow()

    // Transfer data
    private val _recipientPhoneNumber = MutableStateFlow("")
    val recipientPhoneNumber: StateFlow<String> = _recipientPhoneNumber.asStateFlow()

    private val _recipientName = MutableStateFlow("")
    val recipientName: StateFlow<String> = _recipientName.asStateFlow()

    private val _amount = MutableStateFlow(0.0)
    val amount: StateFlow<Double> = _amount.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    init {
        loadCurrentBalance()
    }

    // ============ BALANCE ============

    private fun loadCurrentBalance() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            val result = cardRepository.getUserCards(userId)
            if (result.isSuccess) {
                val cards = result.getOrNull()
                if (!cards.isNullOrEmpty()) {
                    _currentBalance.value = cards.first().balance
                    Log.d("TransferViewModel", "Balance: ${_currentBalance.value}")
                }
            }
        }
    }

    // ============ RECENT CONTACTS ============

    fun loadRecentTransfers() {
        viewModelScope.launch {
            _uiState.value = TransferUiState.Loading

            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val result = transactionRepository.getUserTransactions(userId)

                if (result.isSuccess) {
                    val transactions = result.getOrNull() ?: emptyList()

                    // Son 20 unique transfer
                    val recentList = transactions
                        .filter { it.type == TransactionType.TRANSFER }
                        .filter { it.toUserId != userId }
                        .take(20)
                        .mapNotNull { tx ->
                            val userResult = firestoreDataSource.getUser(tx.toUserId)
                            if (userResult.isSuccess) {
                                val user = userResult.getOrNull()
                                user?.let {
                                    ContactWithLeo(
                                        name = it.fullName,
                                        phoneNumber = it.phoneNumber,
                                        userId = it.userId,
                                        hasLeoAccount = true,
                                        lastTransferDate = tx.timestamp
                                    )
                                }
                            } else null
                        }
                        .distinctBy { it.phoneNumber }

                    _recentContacts.value = recentList
                    _filteredContacts.value = recentList
                    _uiState.value = TransferUiState.Success

                    Log.d("TransferViewModel", "Recent transfers loaded: ${recentList.size}")
                } else {
                    _uiState.value = TransferUiState.Success
                }
            } catch (e: Exception) {
                Log.e("TransferViewModel", "Error loading recent: ${e.message}")
                _uiState.value = TransferUiState.Error(e.message ?: "Xəta")
            }
        }
    }

    fun searchContacts(query: String) {
        if (query.isBlank()) {
            _filteredContacts.value = _recentContacts.value
        } else {
            _filteredContacts.value = _recentContacts.value.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.phoneNumber.contains(query)
            }
        }
    }

    // ============ RECIPIENT SELECTION ============

    fun selectRecipient(contact: ContactWithLeo) {
        _recipientPhoneNumber.value = contact.phoneNumber
        _recipientName.value = contact.name
        Log.d("TransferViewModel", "Selected: ${contact.name} - ${contact.phoneNumber}")
    }

    fun setManualRecipient(phoneNumber: String) {
        _recipientPhoneNumber.value = phoneNumber
        _recipientName.value = "" // Manual input-da ad yoxdur
        Log.d("TransferViewModel", "Manual phone: $phoneNumber")
    }

    // ============ FIND USER BY CARD NUMBER ============

    fun findUserByCardNumber(cardNumber: String) {
        viewModelScope.launch {
            _uiState.value = TransferUiState.Loading

            Log.d("TransferViewModel", "====== SEARCHING CARD ======")
            Log.d("TransferViewModel", "Card number: $cardNumber")

            try {
                // 1. Kartı tap
                val cardResult = cardRepository.getCardByNumber(cardNumber)

                if (cardResult.isFailure) {
                    _uiState.value = TransferUiState.Error("Kart tapılmadı")

                    return@launch
                }

                val card = cardResult.getOrNull()!!
                Log.d("TransferViewModel", "✅ Card found: ${card.cardId}, Owner: ${card.userId}")

                // 2. Kartın sahibini tap
                val userResult = firestoreDataSource.getUser(card.userId)

                if (userResult.isFailure) {
                    _uiState.value = TransferUiState.Error("Kart sahibi tapılmadı")

                    return@launch
                }

                val user = userResult.getOrNull()!!

                // 3. Öz kartına göndərməyi yoxla
                val currentUserId = auth.currentUser?.uid
                if (card.userId == currentUserId) {
                    _uiState.value = TransferUiState.Error("Öz kartınıza köçürə bilməzsiniz")

                    return@launch
                }

                // 4. Recipient məlumatlarını set et
                _recipientPhoneNumber.value = user.phoneNumber
                _recipientName.value = user.fullName
                _uiState.value = TransferUiState.RecipientFound

                Log.d("TransferViewModel", "✅ Recipient found: ${user.fullName} (${user.phoneNumber})")

            } catch (e: Exception) {

                _uiState.value = TransferUiState.Error("Xəta baş verdi: ${e.message}")
            }
        }
    }

    // ============ AMOUNT & DESCRIPTION ============

    fun setAmount(amount: Double) {
        _amount.value = amount
        Log.d("TransferViewModel", "Amount set: $amount")
    }



    // ============ VALIDATION ============

    fun validateAndProceed(): Boolean {
        return when {
            _recipientPhoneNumber.value.isEmpty() -> {
                _uiState.value = TransferUiState.Error("Telefon nömrəsi daxil edin")
                false
            }
            _amount.value <= 0 -> {
                _uiState.value = TransferUiState.Error("Məbləğ daxil edin")
                false
            }
            _amount.value > _currentBalance.value -> {
                _uiState.value = TransferUiState.Error("Balans kifayət deyil")
                false
            }
            else -> true
        }
    }

    // ============ PERFORM TRANSFER ============

    fun performTransfer() {
        if (!validateAndProceed()) return

        viewModelScope.launch {
            _uiState.value = TransferUiState.Loading

            val userId = auth.currentUser?.uid ?: return@launch

            Log.d("TransferViewModel", "====== TRANSFER START ======")
            Log.d("TransferViewModel", "From: $userId")
            Log.d("TransferViewModel", "To: ${_recipientPhoneNumber.value}")
            Log.d("TransferViewModel", "Amount: ${_amount.value}")

            // Use case istifadə et
            val result = transferMoneyUseCase(
                fromUserId = userId,
                toPhoneNumber = _recipientPhoneNumber.value,
                amount = _amount.value,
                description = _description.value
            )

            if (result.isSuccess) {
                val transaction = result.getOrNull()!!
                _uiState.value = TransferUiState.TransferSuccess(transaction)


                // Balance-i yenilə
                loadCurrentBalance()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Köçürmə uğursuz"
                _uiState.value = TransferUiState.Error(error)

            }
        }
    }

    // ============ RESET ============

    fun resetState() {
        _uiState.value = TransferUiState.Idle
    }

    fun clearTransferData() {
        _recipientPhoneNumber.value = ""
        _recipientName.value = ""
        _amount.value = 0.0
        _description.value = ""
    }

    fun refreshBalance() {
        Log.d("TransferViewModel", "Refreshing balance...")
        loadCurrentBalance()
    }
}

// ============ UI STATE ============

sealed class TransferUiState {
    object Idle : TransferUiState()
    object Loading : TransferUiState()
    object Success : TransferUiState()
    object RecipientFound : TransferUiState()
    data class Error(val message: String) : TransferUiState()
    data class TransferSuccess(val transaction: Transaction) : TransferUiState()
}