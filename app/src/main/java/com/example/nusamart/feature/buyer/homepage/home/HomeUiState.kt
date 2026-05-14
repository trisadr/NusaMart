package com.example.nusamart.feature.buyer.homepage.home

import com.example.nusamart.feature.buyer.homepage.ProductCardUiModel

data class HomeUiState(
    val isLoading: Boolean = true,
    val products: List<ProductCardUiModel> = emptyList(),
    val searchQuery: String = ""
)