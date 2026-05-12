package com.example.nusamart.data.model.order

data class Order(
    val idOrder: String,                // PK
    val idUser: String,                 // FK (User)
    val idStore: String,                // FK (Store)
    val idAddress: String,              // FK (UserAddress)
    val invoiceNumber: String,
    val orderDate: java.time.LocalDateTime,
    val arrivedDate: java.time.LocalDateTime? = null,
    val orderStatus: OrderStatus,
    val productTotalPrice: Double,
    val shippingCost: Double,
    val servicePrice: Double,
    val grandTotal: Double,
    val buyerNote: String? = null,
    val createAt: java.time.LocalDateTime,
    val updateAt: java.time.LocalDateTime
) {
    enum class OrderStatus {
        PENDING,
        PROCESSED,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }
}
