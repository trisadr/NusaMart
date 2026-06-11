package com.example.nusamart.feature.seller.order.detail

import com.example.nusamart.data.dto.OrderDto
import com.example.nusamart.data.dto.OrderItemDto
import com.example.nusamart.data.dto.UserProfileResponse

data class IncomingOrderDetailUiState(
    val isLoading: Boolean = true,
    val order: OrderDto? = null,
    val buyer: UserProfileResponse? = null,
    val items: List<OrderItemDto> = emptyList(),
    val errorMessage: String? = null
)