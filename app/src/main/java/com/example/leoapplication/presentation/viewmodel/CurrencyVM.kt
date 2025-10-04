package com.example.leoapplication.presentation.viewmodel

//import androidx.lifecycle.*
//import com.example.leoapplication.data.remote.CurrencyResponse
//import com.example.leoapplication.domain.usecase.GetCurrencyRatesUseCase
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import javax.inject.Inject

//
//@HiltViewModel
//class CurrencyVM @Inject constructor(
//    private val getCurrencyRatesUseCase: GetCurrencyRatesUseCase
//) : ViewModel() {
//
//    private val _rates = MutableLiveData<CurrencyResponse>()
//    val rates: LiveData<CurrencyResponse> = _rates
//
//    private val _error = MutableLiveData<String>()
//    val error: LiveData<String> = _error
//
//    fun fetchRates() {
//        viewModelScope.launch {
//            try {
//                val response = getCurrencyRatesUseCase()
//                _rates.value = response
//            } catch (e: Exception) {
//                _error.value = e.localizedMessage ?: "Xəta baş verdi"
//            }
//        }
//    }
//}
