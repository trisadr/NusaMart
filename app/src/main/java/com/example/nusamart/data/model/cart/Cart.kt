package com.example.nusamart.data.model.cart

data class Cart(
    val idCart: String,         // PK
    val idUser: String          // FK (User)
)