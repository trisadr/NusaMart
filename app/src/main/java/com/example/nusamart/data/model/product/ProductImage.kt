package com.example.nusamart.data.model.product

data class ProductImage(
    val idImage: String,        // PK
    val idProduct: String,      // FK (Product)
    val imageURL: Int,
    val isPrimary: Boolean
)