package com.example.nusamart.feature.seller.homepage

import com.example.nusamart.data.dto.SellerDto
import com.example.nusamart.data.dto.UserProfileResponse

data class SellerHomeUiState(
    val isLoading: Boolean = true,
    val user: UserProfileResponse? = null,
    val sellerInfo: SellerDto? = null,

    val newOrdersCount: Int = 0,
    val totalRevenue: Long = 0,
    val productsSoldCount: Int = 0
)