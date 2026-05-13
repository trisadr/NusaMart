package com.example.nusamart.feature.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.core.MyApplication
import com.example.nusamart.data.repository.user.RegisterResult
import com.example.nusamart.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterVM(
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                RegisterVM(
                    userRepository = app.userRepository
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _successEvent = MutableSharedFlow<Unit>()
    val successEvent = _successEvent.asSharedFlow()

    fun updateUsername(value: String) = _uiState.update { it.copy(username = value) }
    fun updateEmail(value: String) = _uiState.update { it.copy(email = value) }

    // --- Update Phone dengan Filter Angka ---
    fun updatePhone(value: String) {
        // Hanya ambil karakter yang merupakan angka
        val filteredValue = value.filter { it.isDigit() }
        // Batasi maksimal panjang string menjadi 12 digit
        if (filteredValue.length <= 12) {
            _uiState.update { it.copy(phone = filteredValue) }
        }
    }

    fun updatePassword(value: String) = _uiState.update { it.copy(password = value) }
    fun updateConfirmPassword(value: String) = _uiState.update { it.copy(confirmPassword = value) }
    fun toggleRole(isSeller: Boolean) = _uiState.update { it.copy(isSeller = isSeller) }
    fun togglePasswordVisibility() = _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    fun toggleConfirmPasswordVisibility() = _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    fun clearDialog() = _uiState.update { it.copy(dialogState = RegisterDialogState.None) }

    fun register() = viewModelScope.launch {
        val state = _uiState.value

        // --- Validasi Input ---
        if (state.username.isBlank()) return@launch showErrorDialog("Username wajib diisi.")
        if (state.email.isBlank()) return@launch showErrorDialog("Email wajib diisi.")

        // Validasi panjang no telepon (minimal 11 digit, maksimal sudah dibatasi 12 di fungsi updatePhone)
        if (state.phone.length < 11) {
            return@launch showErrorDialog("Nomor telepon tidak valid. Pastikan berisi 11-12 digit angka.")
        }

        if (state.password.isBlank()) return@launch showErrorDialog("Password wajib diisi.")
        if (state.confirmPassword.isBlank()) return@launch showErrorDialog("Konfirmasi password wajib diisi.")
        if (!isEmailValid(state.email)) return@launch showErrorDialog("Format email tidak valid. Pastikan mengandung '@' dan domain yang benar.")

        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(dialogState = RegisterDialogState.PasswordMismatch) }
            return@launch
        }

        _uiState.update { it.copy(isLoading = true) }

        val result = userRepository.register(
            username = state.username,
            email = state.email,
            phone = state.phone,
            password = state.password,
            isSeller = state.isSeller
        )

        when (result) {
            is RegisterResult.Success -> {
                _uiState.update { it.copy(isLoading = false) }
                _successEvent.emit(Unit)
            }
            is RegisterResult.ErrorDuplicate -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        dialogState = RegisterDialogState.DuplicateAccount(result.message)
                    )
                }
            }
        }
    }

    private fun showErrorDialog(msg: String) {
        _uiState.update { it.copy(dialogState = RegisterDialogState.FormError(msg)) }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
        return emailRegex.matches(email)
    }
}