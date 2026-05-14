package com.example.nusamart.feature.auth.login

data class LoginUiState(
    val emailOrUsername: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val dialogState: LoginDialogState = LoginDialogState.None
)

sealed class LoginDialogState {
    object None : LoginDialogState()
    data class Error(val title: String, val message: String) : LoginDialogState()
}