package com.example.leoapplication.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.data.model.User
import com.example.leoapplication.domain.repository.HomeRepository
import com.example.leoapplication.util.Resource
import com.example.leoapplication.util.ThemeHelper
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData.asStateFlow()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            when (val result = homeRepository.getUserData()) {
                is Resource.Success -> {
                    _userData.value = result.data
                    _uiState.value = ProfileUiState.Success
                }
                is Resource.Error -> {
                    _uiState.value = ProfileUiState.Error(result.message ?: "Xəta")
                }
                is Resource.Loading -> {
                    _uiState.value = ProfileUiState.Loading
                }
            }
        }
    }

    fun logout() {
        auth.signOut()
        _uiState.value = ProfileUiState.LoggedOut
    }

    fun isDarkTheme(): Boolean = ThemeHelper.isDarkTheme(context)

    fun saveTheme(isDark: Boolean) = ThemeHelper.setTheme(context, isDark)
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    object Success : ProfileUiState()
    object LoggedOut : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}