package com.example.nusamart.feature.auth.register

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isSeller: Boolean = false,

    // Data tambahan khusus penjual
    val nik: String = "",
    val bankName: String = "",
    val accountNumber: String = "",

    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val dialogState: RegisterDialogState = RegisterDialogState.None
)

sealed class RegisterDialogState {
    object None : RegisterDialogState()
    object PasswordMismatch : RegisterDialogState()
    object PasswordTooShort : RegisterDialogState()
    data class FormError(val message: String) : RegisterDialogState()
    data class ApiError(val message: String) : RegisterDialogState()
}