package com.example.nusamart.feature.auth.register


data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isSeller: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val dialogState: RegisterDialogState = RegisterDialogState.None
)

sealed class RegisterDialogState {
    object None : RegisterDialogState()
    data class FormError(val message: String) : RegisterDialogState()
    object PasswordMismatch : RegisterDialogState()
    data class DuplicateAccount(val message: String) : RegisterDialogState()
}