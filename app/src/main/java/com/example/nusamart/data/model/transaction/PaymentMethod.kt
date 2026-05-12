package com.example.nusamart.data.model.transaction

data class PaymentMethod(
    val idMethod: String,       // PK
    val methodName: String,
    val provider: Provider,
    val isActive: Boolean
) {
    enum class Provider {
        MIDTRANS,
        MANUAL
    }
}
