package com.example.nusamart.data.model.shipping

data class ShippingTracking(
    val idTracking: String,     // PK
    val idShipping: String,     // FK (Shipping)
    val packetLocation: String? = null,
    val description: String? = null,
    val updateAt: java.time.LocalDateTime
)
