package com.example.nusamart.feature.buyer.transaction.courier

import com.example.nusamart.data.dto.CourierOptionDto

data class CourierOptionUiState(
    val couriers: List<CourierOptionDto> = emptyList(),
    val isLoading: Boolean = true
)
