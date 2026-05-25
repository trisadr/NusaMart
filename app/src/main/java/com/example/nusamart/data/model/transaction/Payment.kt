package com.example.nusamart.data.model.transaction

data class Payment(
    val idPayment: String,                      // PK
    val idUser: String,                         // FK (User)
    val idMethod: String,                       // FK (PaymentMethod)
    val totalAmount: Double,                    // Total Tagihan Pembayaran
    val transactionIdGateway: String? = null,
    val snapToken: String? = null,
    val paymentStatus: PaymentStatus,
    val paymentTime: java.time.LocalDateTime? = null
) {
    enum class PaymentStatus {
        PENDING, APPROVED, CANCELED
    }
}