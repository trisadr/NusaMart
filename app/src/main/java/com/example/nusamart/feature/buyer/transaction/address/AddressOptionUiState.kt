package com.example.nusamart.feature.buyer.transaction.address

import com.example.nusamart.data.model.user.UserAddressJson

// --- STATE ---
data class AddressOptionUiState(
    val addresses: List<UserAddressJson> = emptyList(),
    val isLoading: Boolean = true
)

