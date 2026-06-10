package com.example.nusamart.feature.buyer.transaction.address

import com.example.nusamart.data.dto.AddressDto

data class AddressOptionUiState(
    val addresses: List<AddressDto> = emptyList(),
    val isLoading: Boolean = true
)

