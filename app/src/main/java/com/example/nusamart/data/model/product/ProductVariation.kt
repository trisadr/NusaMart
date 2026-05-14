package com.example.nusamart.data.model.product

data class ProductVariation(
    val idVariation: String,    // PK
    val idItem: String,         // FK (ProductItem)
    val typeVariation: String,
    val value: String
)