package com.example.nusamart.feature.seller.order.list

import com.example.nusamart.data.repository.order.OrderJson

data class IncomingOrderListUiState(
    val isLoading: Boolean = true,
    val orders: List<IncomingOrderListUiModel> = emptyList(),
    val selectedFilter: String = "Semua"
)

data class IncomingOrderListUiModel(
    val order: OrderJson,
    val buyerName: String,
    val firstItemName: String,
    val additionalItemCount: Int
)