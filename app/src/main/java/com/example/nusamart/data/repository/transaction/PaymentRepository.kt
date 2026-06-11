package com.example.nusamart.data.repository.transaction

import com.example.nusamart.data.dto.CreatePaymentRequest
import com.example.nusamart.data.dto.PaymentDto
import com.example.nusamart.data.dto.PaymentMethodDto
import com.example.nusamart.data.dto.UpdatePaymentStatusRequest
import com.example.nusamart.data.interfaceapi.PaymentApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class PaymentResult {
    data class Success(val transactionId: String) : PaymentResult()
    data class Error(val message: String) : PaymentResult()
}

@Singleton
class PaymentRepository @Inject constructor(
    private val apiService: PaymentApi
) {

    suspend fun getActivePaymentMethods(): List<PaymentMethodDto> = withContext(Dispatchers.IO) {
        apiService.getPaymentMethods()
    }

    suspend fun getPaymentById(paymentId: String): PaymentDto? = withContext(Dispatchers.IO) {
        try {
            apiService.getPaymentById(paymentId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getPaymentByOrderId(orderId: String): PaymentDto? = withContext(Dispatchers.IO) {
        try {
            apiService.getPaymentByOrderId(orderId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createPayment(
        methodId: String,
        totalAmount: Double,
        transactionIdGateway: String? = null,
        snapToken: String? = null,
        imageURL: String? = null
    ): PaymentResult = withContext(Dispatchers.IO) {
        try {
            val request = CreatePaymentRequest(
                idMethod = methodId,
                totalAmount = totalAmount,
                transactionIdGateway = transactionIdGateway,
                snapToken = snapToken,
                imageURL = imageURL
            )
            val response = apiService.createPayment(request)
            val paymentId = response.payment?.idPayment
                ?: return@withContext PaymentResult.Error("Payment ID tidak ditemukan")
            PaymentResult.Success(paymentId)
        } catch (e: Exception) {
            PaymentResult.Error(e.message ?: "Gagal membuat payment")
        }
    }

    suspend fun updatePaymentStatus(
        paymentId: String,
        newStatus: String // "PENDING" | "APPROVED" | "CANCELED"
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = UpdatePaymentStatusRequest(paymentStatus = newStatus)
            apiService.updatePaymentStatus(paymentId, request)
            true
        } catch (e: Exception) {
            false
        }
    }
}