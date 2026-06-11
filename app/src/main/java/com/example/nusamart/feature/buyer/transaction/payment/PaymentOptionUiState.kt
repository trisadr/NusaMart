package com.example.nusamart.feature.buyer.transaction.payment

import com.example.nusamart.data.dto.PaymentMethodDto

data class PaymentOptionUiState(
    val methods: List<PaymentMethodDto> = emptyList(),
    val isLoading: Boolean = true
)
