package com.example.leoapplication.presentation.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.data.model.Card
import com.example.leoapplication.data.model.User
import com.example.leoapplication.data.remote.FirestoreDataSource
import com.example.leoapplication.domain.usecase.auth.CreateUserUseCase
import com.example.leoapplication.domain.usecase.auth.SendOtpUseCase
import com.example.leoapplication.domain.usecase.auth.VerifyOtpUseCase
import com.example.leoapplication.util.Resource
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val firestoreDataSource: FirestoreDataSource

) : ViewModel() {

    private val _verificationState = MutableLiveData<VerificationState>()
    val verificationState: LiveData<VerificationState> = _verificationState

    private val _authState = MutableLiveData<Resource<String>>()
    val authState: LiveData<Resource<String>> = _authState

    private val _userCreationState = MutableLiveData<Resource<Card>>()
    val userCreationState: LiveData<Resource<Card>> = _userCreationState

    var storedVerificationId: String? = null
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null


    fun sendVerificationCode(
        phoneNumber: String,
        activity: androidx.fragment.app.FragmentActivity
    ) {
        _verificationState.value = VerificationState.CodeSending

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                verifyPhoneNumber(credential)
            }

            override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                _verificationState.value = VerificationState.CodeSendFailed(
                    e.message ?: "Doğrulama uğursuz oldu"
                )
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                android.util.Log.d("PhoneAuthVM", " TEST: Kod göndərildi! ID: $verificationId")
                storedVerificationId = verificationId
                resendToken = token
                _verificationState.value = VerificationState.CodeSent
            }
        }

        sendOtpUseCase(phoneNumber, activity, callbacks)
    }



    fun verifyCode(code: String) {
        val verificationId = storedVerificationId
        if (verificationId.isNullOrEmpty()) {
            _authState.value = Resource.Error("Doğrulama ID-si tapılmadı")
            return
        }

        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        verifyPhoneNumber(credential)
    }


    private fun verifyPhoneNumber(credential: PhoneAuthCredential) {
        _authState.value = Resource.Loading()

        viewModelScope.launch {
            val result = verifyOtpUseCase(credential)

            _authState.value = if (result.isSuccess) {
                Resource.Success(result.getOrNull()!!)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Giriş uğursuz")
            }
        }
    }

    fun createUserProfile(user: User) {
        _userCreationState.value = Resource.Loading()

        viewModelScope.launch {
            val result = createUserUseCase(user)

            _userCreationState.value = if (result.isSuccess) {
                Resource.Success(result.getOrNull()!!)
            } else {
                Resource.Error(
                    result.exceptionOrNull()?.message ?: "Profil yaradılmadı"
                )
            }
        }


    }

    suspend fun checkIfUserExists(userId: String): Boolean {
        return try {
            val result = firestoreDataSource.getUser(userId)
            result.isSuccess && result.getOrNull() != null
        } catch (e: Exception) {
            false
        }
    }


    sealed class VerificationState {
        object CodeSending : VerificationState()
        object CodeSent : VerificationState()
        data class CodeSendFailed(val message: String) : VerificationState()
    }
}