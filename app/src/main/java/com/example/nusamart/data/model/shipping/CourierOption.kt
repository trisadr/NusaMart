package com.example.nusamart.data.model.shipping

data class CourierOption(
    val idCourier: String,      // PK
    val courierName: String,
    val serviceType: ServiceType,
    val timeEstimation: String,
    val isActive: Boolean
) {
    enum class ServiceType {
        REGULAR,
        KARGO
    }
}
