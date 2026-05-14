package com.example.nusamart.data.model.cart

data class CartItem(
    val idCartItem: String,     // PK
    val idCart: String,         // FK (Cart)
    val idItem: String,         // FK (ProductItem)
    val quantity: Int,
    val isChecked: Boolean
)
