package com.example.nusamart.entity

data class Product(
    val idProduct: String,
    val name: String,
    val price: Double,
    val description: String,
    val stock: Int,
    val map: String, // Asumsi ini link map atau koordinat
    val imageResId: Int, // Int untuk resource ID drawable/res
    val idSeller: String,
    val idStore: String,
    val location: String
)