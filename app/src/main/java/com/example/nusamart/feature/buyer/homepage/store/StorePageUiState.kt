package com.example.nusamart.feature.buyer.homepage.store

import com.example.nusamart.feature.buyer.homepage.ProductCardUiModel

data class StorePageUiState(
    val isLoading: Boolean = true,
    val storeId: String = "",
    val storeName: String = "",
    val storeLocation: String = "",
    val storeRating: Double = 0.0,
    val isVerified: Boolean = false,
    val storeProducts: List<ProductCardUiModel> = emptyList()
)