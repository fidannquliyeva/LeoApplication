package com.example.leoapplication.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.data.model.Payment
import com.example.leoapplication.data.model.PaymentCategory
import com.example.leoapplication.data.model.ServiceProvider
import com.example.leoapplication.domain.usecase.payment.GetPaymentCategoriesUseCase
import com.example.leoapplication.domain.usecase.payment.GetPaymentHistoryUseCase
import com.example.leoapplication.domain.usecase.payment.GetProvidersUseCase
import com.example.leoapplication.domain.usecase.payment.MakePaymentUseCase
import com.example.leoapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val getPaymentCategoriesUseCase: GetPaymentCategoriesUseCase,
    private val getProvidersUseCase: GetProvidersUseCase,
    private val makePaymentUseCase: MakePaymentUseCase,
    private val getPaymentHistoryUseCase: GetPaymentHistoryUseCase
) : ViewModel() {

    private val _categoriesState = MutableLiveData<Resource<List<PaymentCategory>>>()
    val categoriesState: LiveData<Resource<List<PaymentCategory>>> = _categoriesState

    private val _providersState = MutableLiveData<Resource<List<ServiceProvider>>>()
    val providersState: LiveData<Resource<List<ServiceProvider>>> = _providersState

    private val _paymentState = MutableLiveData<Resource<Payment>>()
    val paymentState: LiveData<Resource<Payment>> = _paymentState

    private val _paymentHistoryState = MutableLiveData<Resource<List<Payment>>>()
    val paymentHistoryState: LiveData<Resource<List<Payment>>> = _paymentHistoryState


    fun loadCategories() {
        _categoriesState.value = Resource.Loading()

        viewModelScope.launch {
            val result = getPaymentCategoriesUseCase()

            _categoriesState.value = if (result.isSuccess) {
                Resource.Success(result.getOrNull()!!)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Kateqoriyalar yüklənmədi")
            }
        }
    }


    fun loadProviders(categoryId: String) {
        _providersState.value = Resource.Loading()

        viewModelScope.launch {
            val result = getProvidersUseCase(categoryId)

            _providersState.value = if (result.isSuccess) {
                Resource.Success(result.getOrNull()!!)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Provayderlar yüklənmədi")
            }
        }
    }

    fun makePayment(
        userId: String,
        cardId: String,
        providerId: String,
        accountNumber: String,
        amount: Double
    ) {
        _paymentState.value = Resource.Loading()

        viewModelScope.launch {
            val result = makePaymentUseCase(userId, cardId, providerId, accountNumber, amount)

            _paymentState.value = if (result.isSuccess) {
                Resource.Success(result.getOrNull()!!)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Ödəniş uğursuz oldu")
            }
        }
    }

    fun loadPaymentHistory(userId: String) {
        _paymentHistoryState.value = Resource.Loading()

        viewModelScope.launch {
            val result = getPaymentHistoryUseCase(userId)

            _paymentHistoryState.value = if (result.isSuccess) {
                Resource.Success(result.getOrNull()!!)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Tarixçə yüklənmədi")
            }
        }
    }
}