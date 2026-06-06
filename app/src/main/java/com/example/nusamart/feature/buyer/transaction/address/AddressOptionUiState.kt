package com.example.nusamart.feature.buyer.transaction.address

import com.example.nusamart.data.repository.user.UserAddressJson

data class AddressOptionUiState(
    val addresses: List<UserAddressJson> = emptyList(),
    val isLoading: Boolean = true
)

