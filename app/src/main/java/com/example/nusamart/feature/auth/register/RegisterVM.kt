package com.example.nusamart.feature.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.repository.user.RegisterResult
import com.example.nusamart.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterVM @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _successEvent = MutableSharedFlow<Unit>()
    val successEvent = _successEvent.asSharedFlow()

    fun updateUsername(value: String) = _uiState.update { it.copy(username = value) }
    fun updateEmail(value: String) = _uiState.update { it.copy(email = value) }
    fun updatePhone(value: String) {
        val filteredValue = value.filter { it.isDigit() }
        if (filteredValue.length <= 13) {
            _uiState.update { it.copy(phone = filteredValue) }
        }
    }

    // --- State Khusus Penjual ---
    fun updateNik(value: String) = _uiState.update { it.copy(nik = value.filter { char -> char.isDigit() }.take(16)) }
    fun updateBankName(value: String) = _uiState.update { it.copy(bankName = value) }
    fun updateAccountNumber(value: String) = _uiState.update { it.copy(accountNumber = value.filter { char -> char.isDigit() }.take(20)) }

    fun updatePassword(value: String) = _uiState.update { it.copy(password = value) }
    fun updateConfirmPassword(value: String) = _uiState.update { it.copy(confirmPassword = value) }
    fun toggleRole(isSeller: Boolean) = _uiState.update { it.copy(isSeller = isSeller) }
    fun togglePasswordVisibility() = _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    fun toggleConfirmPasswordVisibility() = _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    fun clearDialog() = _uiState.update { it.copy(dialogState = RegisterDialogState.None) }

    fun register() = viewModelScope.launch {
        val state = _uiState.value

        // --- Validasi Input Umum ---
        if (state.username.isBlank()) return@launch showErrorDialog("Username wajib diisi.")
        if (state.email.isBlank()) return@launch showErrorDialog("Email wajib diisi.")
        if (state.phone.length < 10) return@launch showErrorDialog("Nomor telepon tidak valid.")
        if (state.password.isBlank()) return@launch showErrorDialog("Password wajib diisi.")
        if (state.confirmPassword.isBlank()) return@launch showErrorDialog("Konfirmasi password wajib diisi.")
        if (!isEmailValid(state.email)) return@launch showErrorDialog("Format email tidak valid.")
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(dialogState = RegisterDialogState.PasswordMismatch) }
            return@launch
        }

        // --- Validasi Khusus Penjual ---
        if (state.isSeller) {
            if (state.nik.length != 16) return@launch showErrorDialog("NIK harus 16 digit angka.")
            if (state.bankName.isBlank()) return@launch showErrorDialog("Nama Bank wajib diisi.")
            if (state.accountNumber.length < 10) return@launch showErrorDialog("Nomor Rekening minimal 10 digit angka.")
        }

        _uiState.update { it.copy(isLoading = true) }

        // Panggil ke Repository (mengirim data bank jika role-nya Seller)
        val result = userRepository.register(
            username = state.username,
            email = state.email,
            phone = state.phone,
            password = state.password,
            isSeller = state.isSeller,
            nik = if (state.isSeller) state.nik else null,
            bankName = if (state.isSeller) state.bankName else null,
            accountNumber = if (state.isSeller) state.accountNumber else null
        )

        when (result) {
            is RegisterResult.Success -> {
                _uiState.update { it.copy(isLoading = false) }
                _successEvent.emit(Unit)
            }
            is RegisterResult.ErrorDuplicate -> {
                _uiState.update {
                    it.copy(isLoading = false, dialogState = RegisterDialogState.DuplicateAccount(result.message))
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