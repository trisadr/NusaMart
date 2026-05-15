package com.example.nusamart.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.core.MyApplication
import com.example.nusamart.data.repository.user.LoginResult
import com.example.nusamart.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginVM(
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                LoginVM(userRepository = app.userRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    // Event sukses akan mengirimkan ROLE
    private val _successEvent = MutableSharedFlow<String>()
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
                // Kirim role ke UI agar bisa diarahkan ke halaman yang tepat
                _successEvent.emit(result.role)
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