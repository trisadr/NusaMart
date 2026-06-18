package com.example.nusamart.data.dto

import com.google.gson.annotations.SerializedName

// RESPONSE (Balasan dari Server)

data class CourierOptionDto(
    @SerializedName("idCourier") val idCourier: String,
    @SerializedName("courierName") val courierName: String,
    @SerializedName("serviceType") val serviceType: String,
    @SerializedName("timeEstimation") val timeEstimation: String,
    @SerializedName("isActive") val isActive: Int
)

data class ShippingDto(
    @SerializedName("idShipping") val idShipping: String,
    @SerializedName("idOrder") val idOrder: String,
    @SerializedName("idCourier") val idCourier: String,
    @SerializedName("resi") val resi: String?,
    @SerializedName("shippingPrice") val shippingPrice: Double,
    @SerializedName("shippingStatus") val shippingStatus: String,
    @SerializedName("shippingDate") val shippingDate: String?,
    @SerializedName("deliveredDate") val deliveredDate: String?,

    // Tangkapan Eager Loading dari Laravel (fungsi: with('shippingTrackings'))
    @SerializedName("shipping_trackings") val trackings: List<ShippingTrackingDto>? = null
)

data class ShippingTrackingDto(
    @SerializedName("idTracking") val idTracking: String,
    @SerializedName("idShipping") val idShipping: String,
    @SerializedName("packetLocation") val packetLocation: String?,
    @SerializedName("description") val description: String,
    @SerializedName("updateAt") val updateAt: String
)

data class ShippingActionResponse(
    @SerializedName("message") val message: String,
    @SerializedName("shipping") val shipping: ShippingDto?
)

data class TrackingActionResponse(
    @SerializedName("message") val message: String,
    @SerializedName("tracking") val tracking: ShippingTrackingDto?
)


// REQUEST (Data yang dikirim ke Server)

data class CreateShippingRequest(
    @SerializedName("idOrder") val idOrder: String,
    @SerializedName("idCourier") val idCourier: String
)

data class UpdateShippingStatusRequest(
    @SerializedName("shippingStatus") val shippingStatus: String,
    @SerializedName("resi") val resi: String? = null
)

data class AddTrackingRequest(
    @SerializedName("packetLocation") val packetLocation: String?,
    @SerializedName("description") val description: String
)