package com.example.nusamart.data.dto

import com.google.gson.annotations.SerializedName

// --- RESPONSE ---

data class PaymentMethodDto(
    @SerializedName("idMethod") val idMethod: String,
    @SerializedName("methodName") val methodName: String,
    @SerializedName("provider") val provider: String,
    @SerializedName("isActive") val isActive: Int
)

data class PaymentDto(
    @SerializedName("idPayment") val idPayment: String,
    @SerializedName("idUser") val idUser: String,
    @SerializedName("idMethod") val idMethod: String,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("transactionIdGateway") val transactionIdGateway: String? = null,
    @SerializedName("snapToken") val snapToken: String? = null,
    @SerializedName("paymentStatus") val paymentStatus: String,
    @SerializedName("paymentTime") val paymentTime: String? = null,
    @SerializedName("imageURL") val imageURL: String? = null
)

data class PaymentActionResponse(
    @SerializedName("message") val message: String,
    @SerializedName("payment") val payment: PaymentDto?
)

// --- REQUEST ---

data class CreatePaymentRequest(
    @SerializedName("idMethod") val idMethod: String,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("transactionIdGateway") val transactionIdGateway: String? = null,
    @SerializedName("snapToken") val snapToken: String? = null,
    @SerializedName("imageURL") val imageURL: String? = null
)

data class UpdatePaymentStatusRequest(
    @SerializedName("paymentStatus") val paymentStatus: String
)