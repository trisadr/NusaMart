package com.example.nusamart.entity

data class Product(
    val idProduct: String,
    val name: String,
    val price: Double,
    val description: String,
    val stock: Int,
    val map: String, 
    val imageResId: Int, 
    val idSeller: String,
    val idStore: String,
    val location: String
)
