package com.example.nusamart.entity

data class OrderItem(
    val idOrderItem: String,
    val idOrder: String,
    val idProduct: String,
    val quantity: Int,
    val priceAtPurchase: Double
)