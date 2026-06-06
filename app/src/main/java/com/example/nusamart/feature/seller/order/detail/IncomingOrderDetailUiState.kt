package com.example.nusamart.feature.seller.order.detail

import com.example.nusamart.data.repository.order.OrderItemJson
import com.example.nusamart.data.repository.order.OrderJson
import com.example.nusamart.data.repository.user.UserJson

data class IncomingOrderDetailUiState(
    val isLoading: Boolean = true,
    val order: OrderJson? = null,
    val buyer: UserJson? = null,
    val items: List<OrderItemJson> = emptyList(),
    val errorMessage: String? = null
)