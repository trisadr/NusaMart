package com.example.nusamart.feature.buyer.transaction.courier

import com.example.nusamart.data.repository.shipping.CourierOptionJson

data class CourierOptionUiState(
    val couriers: List<CourierOptionJson> = emptyList(),
    val isLoading: Boolean = true
)
