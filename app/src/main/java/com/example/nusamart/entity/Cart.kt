package com.example.nusamart.entity

data class Cart(
    val idCart: String,
    val idBuyer: String,
    val idProduct: String,
    val quantity: Int,
    val isChecked: Boolean
)