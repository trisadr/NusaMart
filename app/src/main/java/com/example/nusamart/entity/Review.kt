package com.example.nusamart.entity

data class Review(
    val idOrder: String,
    val idProduct: String,
    val imageResId: Int, // Int untuk resource ID drawable/res
    val rating: Double, // Menggunakan Double agar bisa desimal (contoh: 4.5)
    val reviewProduct: String
)