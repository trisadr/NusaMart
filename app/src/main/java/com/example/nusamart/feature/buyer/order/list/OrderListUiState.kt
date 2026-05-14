package com.example.nusamart.feature.buyer.order.list

import com.example.nusamart.data.repository.order.OrderJson

data class OrderListUiState(
    val isLoading: Boolean = true,
    val orders: List<OrderJson> = emptyList(),
    val selectedFilter: String = "Semua"
)