package com.example.nusamart.feature.buyer.homepage

data class ProductCardUiModel(
    val idProduct: String,
    val name: String,
    val price: Double,
    val location: String,
    val imageResId: Int // Menyimpan ID resource gambar
)