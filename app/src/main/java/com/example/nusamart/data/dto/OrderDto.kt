package com.example.nusamart.data.dto

import com.google.gson.annotations.SerializedName

// --- RESPONSE ---

data class OrderDto(
    @SerializedName("idOrder") val idOrder: String,
    @SerializedName("idPayment") val idPayment: String,
    @SerializedName("idUser") val idUser: String,
    @SerializedName("idStore") val idStore: String,
    @SerializedName("idAddress") val idAddress: String,
    @SerializedName("invoiceNumber") val invoiceNumber: String,
    @SerializedName("orderDate") val orderDate: String? = null,
    @SerializedName("arrivedDate") val arrivedDate: String? = null,
    @SerializedName("orderStatus") val orderStatus: String,
    @SerializedName("productTotalPrice") val productTotalPrice: Double,
    @SerializedName("shippingCost") val shippingCost: Double,
    @SerializedName("servicePrice") val servicePrice: Double,
    @SerializedName("grandTotal") val grandTotal: Double,
    @SerializedName("buyerNote") val buyerNote: String? = null,
    @SerializedName("createAt") val createAt: String? = null,
    @SerializedName("updateAt") val updateAt: String? = null,
    @SerializedName("order_items") val orderItems: List<OrderItemDto> = emptyList()
)

data class OrderItemDto(
    @SerializedName("idOrderItem") val idOrderItem: String,
    @SerializedName("idOrder") val idOrder: String,
    @SerializedName("idItem") val idItem: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("nameSnapshot") val nameSnapshot: String,
    @SerializedName("priceSnapshot") val priceSnapshot: Double
)

data class OrderActionResponse(
    @SerializedName("message") val message: String,
    @SerializedName("order") val order: OrderDto?
)

data class IsReviewedResponse(
    @SerializedName("isReviewed") val isReviewed: Boolean
)

// --- REQUEST ---

data class CreateOrderRequest(
    @SerializedName("idStore") val idStore: String,
    @SerializedName("idAddress") val idAddress: String,
    @SerializedName("idPayment") val idPayment: String,
    @SerializedName("shippingCost") val shippingCost: Double,
    @SerializedName("servicePrice") val servicePrice: Double,
    @SerializedName("buyerNote") val buyerNote: String? = null,
    @SerializedName("items") val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    @SerializedName("idItem") val idItem: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("nameSnapshot") val nameSnapshot: String,
    @SerializedName("priceSnapshot") val priceSnapshot: Double
)

data class UpdateOrderStatusRequest(
    @SerializedName("orderStatus") val orderStatus: String
)