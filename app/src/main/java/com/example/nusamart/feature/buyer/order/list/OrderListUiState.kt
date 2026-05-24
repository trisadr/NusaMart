package com.example.nusamart.feature.buyer.order.list

import com.example.nusamart.data.repository.order.OrderJson

data class OrderListUiState(
    val isLoading: Boolean = true,
    val orders: List<OrderListUiModel> = emptyList(),
    val selectedFilter: String = "Semua"
)
data class OrderListUiModel(
    val order: OrderJson,
    val storeName: String,
    val firstItemName: String,
    val additionalItemCount: Int
)