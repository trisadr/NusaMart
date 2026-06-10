package com.example.nusamart.feature.seller.homepage

import com.example.nusamart.data.repository.user.SellerJson
import com.example.nusamart.data.repository.user.UserJson

data class SellerHomeUiState(
    val isLoading: Boolean = true,
    val user: UserJson? = null,
    val sellerInfo: SellerJson? = null,
    val newOrdersCount: Int = 0,
    val productsSold: Int = 0,
    val totalRevenue: Double = 0.0,
    val errorMessage: String? = null
)