package com.example.nusamart.entity

enum class OrderStatus {
    MENUNGGU,
    DIPROSES,
    DIKIRIM,
    SELESAI,
    DIBATALKAN
}

data class Order(
    val idOrder: String,
    val totalPrice: Double,
    val status: OrderStatus, 
    val trackingNumber: String,
    val description: String,
    val orderDate: Long, 
    val arrivedDate: Long?, 
    val idSeller: String
)
