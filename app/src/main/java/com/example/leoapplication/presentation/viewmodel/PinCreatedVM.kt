package com.example.leoapplication.presentation.viewmodel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
//
//@HiltViewModel
//class PinCreatedVM @Inject constructor(
//    private val savePinUseCase: SavePinUseCase,
//    private val getPinUseCase: GetPinUseCase
//) : ViewModel() {
//
//    private val _pin = MutableLiveData<String>()
//    val pin: LiveData<String> get() = _pin
//
//    private val _confirmPin = MutableLiveData<String>()
//    val confirmPin: LiveData<String> get() = _confirmPin
//
//    private val _pinSaved = MutableLiveData<Boolean>()
//    val pinSaved: LiveData<Boolean> get() = _pinSaved
//
//    private val _error = MutableLiveData<String>()
//    val error: LiveData<String> get() = _error
//
//    fun setPin(value: String) { _pin.value = value }
//    fun setConfirmPin(value: String) { _confirmPin.value = value }
//
//    fun savePin() {
//        val p = _pin.value
//        val c = _confirmPin.value
//
//        if (p.isNullOrBlank() || c.isNullOrBlank()) {
//            _error.value = "PIN və təsdiq boş ola bilməz"
//            return
//        }
//
//        if (p != c) {
//            _error.value = "PIN və təsdiq eyni deyil"
//            // confirmPin-i reset et ki, istifadəçi yenidən daxil etsin
//            _confirmPin.value = ""
//            return
//        }
//
//        try {
//            savePinUseCase.execute(p)
//            _pinSaved.value = true
//        } catch (e: Exception) {
//            _error.value = e.message ?: "PIN yadda saxlanmadı"
//        }
//    }
//
//
//    fun getSavedPin(): String? = getPinUseCase.execute()
//
//
//}
