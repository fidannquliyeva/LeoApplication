package com.example.leoapplication.presentation.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    val smsSent = MutableLiveData<Boolean?>()
    val verificationSuccess = MutableLiveData<Boolean>()
    val error = MutableLiveData<String?>()

    var code: String = "000000"

    // Firebase test nömrələri üçün xəritə
    val testNumbers = mapOf(
        "+994708512191" to "121318",
        "+994708512121" to "121304",
        "+994554261071" to "000000",
        "+994705116333" to "111111",
        "+994705113333" to "333333"

    )

    fun sendSms(phone: String) {
        // Əgər Firebase test nömrəsidirsə
        testNumbers[phone]?.let { testCode ->
            smsSent.value = true
            code = testCode
            Toast.makeText(getApplication(), "Test kod: $testCode", Toast.LENGTH_LONG).show()
            return
        }

        // Əks halda real SMS prosesi
        smsSent.value = false
        error.value = "Bu nömrə test nömrəsi deyil."
    }

    fun verifyCode(inputCode: String) {
        verificationSuccess.value = (inputCode == code)
    }
}
