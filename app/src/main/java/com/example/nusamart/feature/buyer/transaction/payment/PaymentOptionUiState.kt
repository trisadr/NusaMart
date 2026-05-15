package com.example.nusamart.feature.buyer.transaction.payment

import com.example.nusamart.data.repository.transaction.PaymentMethodJson

data class PaymentOptionUiState(
    val methods: List<PaymentMethodJson> = emptyList(),
    val isLoading: Boolean = true
)
