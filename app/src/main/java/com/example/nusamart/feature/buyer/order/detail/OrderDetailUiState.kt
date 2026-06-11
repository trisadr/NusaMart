package com.example.nusamart.feature.buyer.order.detail

import com.example.nusamart.data.dto.OrderDto
import com.example.nusamart.data.dto.OrderItemDto

data class OrderDetailUiState(
    val isLoading: Boolean = true,
    val order: OrderDto? = null,
    val orderItems: List<OrderItemDto> = emptyList(),
    val resiNumber: String = "Belum Ada",
    val isReviewed: Boolean = false,
    val errorMessage: String? = null
)