package com.example.nusamart.data.model.shipping

data class Shipping(
    val idShipping: String,     // PK
    val idOrder: String,        // FK (Order)
    val idCourier: String,      // FK (CourierOption)
    val resi: String? = null,
    val shippingDate: java.time.LocalDateTime? = null,
    val deliveredDate: java.time.LocalDateTime? = null,
    val shippingStatus: ShippingStatus
) {
    enum class ShippingStatus {
        WAITING,
        PICKED_UP,
        IN_TRANSIT,
        DELIVERED,
        FAILED
    }
}
