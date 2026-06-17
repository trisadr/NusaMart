package com.example.nusamart.feature.buyer.homepage

data class ProductCardUiModel(
    val idProduct: String,
    val name: String,
    val price: Double,
    val location: String,
    val imageResId: String? = null,
    val rating: Double = 0.0,
    val soldCount: Int = 0
)