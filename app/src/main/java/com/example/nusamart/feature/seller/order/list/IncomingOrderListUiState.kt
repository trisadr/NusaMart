package com.example.nusamart.feature.seller.order.list

import com.example.nusamart.data.dto.OrderDto

data class IncomingOrderListUiState(
    val isLoading: Boolean = true,
    val orders: List<IncomingOrderListUiModel> = emptyList(),
    val selectedFilter: String = "Semua"
)

data class IncomingOrderListUiModel(
    val order: OrderDto,
    val buyerName: String,
    val firstItemName: String,
    val additionalItemCount: Int
)