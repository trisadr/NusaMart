package com.example.nusamart.feature.buyer.order.detail

import com.example.nusamart.data.repository.order.OrderItemJson
import com.example.nusamart.data.repository.order.OrderJson

data class OrderDetailUiState(
    val isLoading: Boolean = true,
    val order: OrderJson? = null,
    val orderItems: List<OrderItemJson> = emptyList(),
    val resiNumber: String = "Belum Ada",
    val isReviewed: Boolean = false,
    val errorMessage: String? = null
)