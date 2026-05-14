package com.example.nusamart.data.model.product

data class ProductItem(
    val idItem: String,         // PK
    val idProduct: String,      // FK (Product)
    val sku: String? = null,
    val stock: Int,
    val price: Double,
    val isActive: Boolean
)