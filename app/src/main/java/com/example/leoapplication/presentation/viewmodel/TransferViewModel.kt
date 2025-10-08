package com.example.leoapplication.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.data.model.Transaction
import com.example.leoapplication.domain.usecase.transaction.TransferMoneyUseCase
import com.example.leoapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val transferMoneyUseCase: TransferMoneyUseCase
) : ViewModel() {

    // Transfer vəziyyəti
    private val _transferState = MutableLiveData<Resource<Transaction>>()
    val transferState: LiveData<Resource<Transaction>> = _transferState

    /**
     * Pul köçürmə
     */
    fun transferMoney(
        fromUserId: String,
        toPhoneNumber: String,
        amount: Double,
        description: String
    ) {
        _transferState.value = Resource.Loading()

        viewModelScope.launch {
            val result = transferMoneyUseCase(fromUserId, toPhoneNumber, amount, description)

            _transferState.value = if (result.isSuccess) {
                Resource.Success(result.getOrNull()!!)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Transfer uğursuz oldu")
            }
        }
    }
}