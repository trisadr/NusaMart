package com.example.nusamart.feature.buyer.homepage.search

import com.example.nusamart.feature.buyer.homepage.ProductCardUiModel

data class SearchResultUiState(
    val isLoading: Boolean = true,
    val currentQuery: String = "",
    val selectedFilter: String = "Semua",
    val filteredProducts: List<ProductCardUiModel> = emptyList()
)