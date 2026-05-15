package com.example.nusamart.feature.buyer.transaction.success

data class SuccessUiState(
    val isLoading: Boolean = true,
    val provider: String = "COD",
    val paymentCode: String = "",
    val bankName: String = ""
)