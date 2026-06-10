package com.example.nusamart.feature.seller.homepage

import com.example.nusamart.data.dto.SellerDto
import com.example.nusamart.data.dto.UserProfileResponse

data class SellerHomeUiState(
    val isLoading: Boolean = true,
    // UBAH: Sesuaikan tipe datanya dengan response API
    val user: UserProfileResponse? = null,
    val sellerInfo: SellerDto? = null,

    val newOrdersCount: Int = 0,
    val totalRevenue: Int = 0,
    val productsSold: Int = 0
)