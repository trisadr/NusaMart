package com.example.nusamart.entity

data class Review(
    val idReview : String,
    val idOrder: String,
    val idProduct: String,
    val imageResId: Int?, // Int untuk resource ID drawable/res
    val rating: Int,
    val reviewProduct: String?
)