package com.example.nusamart.data.model.order

data class OrderItem(
    val idOrderItem: String,    // PK
    val idOrder: String,        // FK (Order)
    val idItem: String,         // FK (ProductItem)
    val quantity: Int,
    val nameSnapshot: String,
    val priceSnapshot: Double
)
