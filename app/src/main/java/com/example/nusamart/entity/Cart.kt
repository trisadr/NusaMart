package com.example.nusamart.entity

data class Cart(
    val idCart: String,
    val idProduct: String,
    val quantity: Int,
    val idUser: String
)