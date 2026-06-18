package com.example.nusamart.feature.seller.homepage

import com.example.nusamart.data.dto.SellerDto
import com.example.nusamart.data.dto.UserProfileResponse

data class SellerHomeUiState(
    val isLoading: Boolean = true,
    val user: UserProfileResponse? = null,
    val sellerInfo: SellerDto? = null,

    // Variabel metrik dinamis
    val newOrdersCount: Int = 0,
    val totalRevenue: Long = 0, // Menggunakan Long untuk uang
    val productsSoldCount: Int = 0 // Jumlah item barang terjual
)