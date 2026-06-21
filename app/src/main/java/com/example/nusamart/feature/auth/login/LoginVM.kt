package com.example.nusamart.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.repository.user.LoginResult
import com.example.nusamart.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
sealed class LoginNavigation {
    object ToBuyerHome : LoginNavigation()
    object ToSellerHome : LoginNavigation()
}

@HiltViewModel
class LoginVM @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _successEvent = MutableSharedFlow<LoginNavigation>()
    val successEvent = _successEvent.asSharedFlow()

    fun updateEmailOrUsername(value: String) = _uiState.update { it.copy(emailOrUsername = value) }
    fun updatePassword(value: String) = _uiState.update { it.copy(password = value) }
    fun togglePasswordVisibility() = _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    fun clearDialog() = _uiState.update { it.copy(dialogState = LoginDialogState.None) }

    fun login() = viewModelScope.launch {
        val state = _uiState.value

        // Validasi Input Kosong
        if (state.emailOrUsername.isBlank() || state.password.isBlank()) {
            _uiState.update {
                it.copy(dialogState = LoginDialogState.Error(
                    title = "Form Belum Lengkap",
                    message = "Email/Username dan Password wajib diisi sebelum login."
                ))
            }
            return@launch
        }

        // Proses Login ke Repository
        _uiState.update { it.copy(isLoading = true) }

        val result = userRepository.login(state.emailOrUsername, state.password)

        // Menangani Hasil
        when (result) {
            is LoginResult.Success -> {
                _uiState.update { it.copy(isLoading = false) }

                // Cek Role dan Arahkan Navigasi Langsung
                if (result.role == "SELLER") {
                    _successEvent.emit(LoginNavigation.ToSellerHome)
                } else {
                    _successEvent.emit(LoginNavigation.ToBuyerHome)
                }
            }
            is LoginResult.Error -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        dialogState = LoginDialogState.Error(title = "Login Gagal", message = result.message)
                    )
                }
            }
        }
    }
}