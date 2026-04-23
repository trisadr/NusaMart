package com.example.nusamart.entity

import com.example.nusamart.entity.OrderStatus

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
    val status: OrderStatus, // Menggunakan Enum
    val trackingNumber: String,
    val description: String,
    val orderDate: Long, // Menggunakan Long (Timestamp) atau String
    val arrivedDate: Long?, // Nullable (?) karena bisa jadi belum sampai
    val idSeller: String
)