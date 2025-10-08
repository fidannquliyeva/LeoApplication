package com.example.leoapplication.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.data.model.Card
import com.example.leoapplication.data.model.Transaction
import com.example.leoapplication.domain.usecase.card.GetUserCardsUseCase
import com.example.leoapplication.domain.usecase.transaction.GetTransactionsUseCase
import com.example.leoapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserCardsUseCase: GetUserCardsUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    // Kartlar vəziyyəti
    private val _cardsState = MutableLiveData<Resource<List<Card>>>()
    val cardsState: LiveData<Resource<List<Card>>> = _cardsState

    // Transaksiyalar vəziyyəti
    private val _transactionsState = MutableLiveData<Resource<List<Transaction>>>()
    val transactionsState: LiveData<Resource<List<Transaction>>> = _transactionsState

    /**
     * İstifadəçinin kartlarını yüklə
     */
    fun loadUserCards(userId: String) {
        _cardsState.value = Resource.Loading()

        viewModelScope.launch {
            val result = getUserCardsUseCase(userId)

            _cardsState.value = if (result.isSuccess) {
                Resource.Success(result.getOrNull()!!)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Kartlar yüklənmədi")
            }
        }
    }

    /**
     * İstifadəçinin transaksiyalarını yüklə
     */
    fun loadTransactions(userId: String) {
        _transactionsState.value = Resource.Loading()

        viewModelScope.launch {
            val result = getTransactionsUseCase(userId)

            _transactionsState.value = if (result.isSuccess) {
                Resource.Success(result.getOrNull()!!)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Transaksiyalar yüklənmədi")
            }
        }
    }
}