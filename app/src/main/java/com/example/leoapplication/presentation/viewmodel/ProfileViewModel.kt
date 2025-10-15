package com.example.leoapplication.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.data.model.User
import com.example.leoapplication.domain.repository.HomeRepository
import com.example.leoapplication.util.AvatarManager
import com.example.leoapplication.util.PinManager
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


    private val _avatarUri = MutableStateFlow<String?>(null)
    val avatarUri: StateFlow<String?> = _avatarUri.asStateFlow()

    init {
        loadUserData()
        loadAvatar()
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
        viewModelScope.launch {
            try {
                // Auth sign out
                val userBeforeLogout = auth.currentUser?.uid
                Log.d("ProfileViewModel", "User before logout: $userBeforeLogout")

                auth.signOut()

                val userAfterLogout = auth.currentUser?.uid
                Log.d("ProfileViewModel", "User after logout: $userAfterLogout (should be null)")

                AvatarManager.clearAvatar(context)
                Log.d("ProfileViewModel", "Avatar cleared")

                val pinBeforeClear = PinManager.isPinSet(context)
                Log.d("ProfileViewModel", "PIN before clear: $pinBeforeClear")

                PinManager.clearPin(context)

                val pinAfterClear = PinManager.isPinSet(context)
                Log.d("ProfileViewModel", "PIN after clear: $pinAfterClear (should be false)")

                // SharedPreferences təmizlə
                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                prefs.edit().clear().apply()
                Log.d("ProfileViewModel", " SharedPreferences cleared")

                Log.d("ProfileViewModel", " LOGOUT COMPLETED - all data cleared")

                _uiState.value = ProfileUiState.LoggedOut
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Logout error: ${e.message}")
                _uiState.value = ProfileUiState.Error("Çıxış zamanı xəta")
            }
        }
    }

    fun isDarkTheme(): Boolean = ThemeHelper.isDarkTheme(context)

    fun saveTheme(isDark: Boolean) = ThemeHelper.setTheme(context, isDark)

    fun saveAvatar(uri: Uri) {
        AvatarManager.saveAvatar(context, uri)
        loadAvatar()
    }

    private fun loadAvatar() {
        _avatarUri.value = AvatarManager.getAvatar(context)
    }

    fun clearAvatar() {
        AvatarManager.clearAvatar(context)
        _avatarUri.value = null
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    object Success : ProfileUiState()
    object LoggedOut : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}