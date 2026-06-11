package com.example.nusamart.data.interfaceapi

import com.example.nusamart.data.dto.CreatePaymentRequest
import com.example.nusamart.data.dto.PaymentActionResponse
import com.example.nusamart.data.dto.PaymentDto
import com.example.nusamart.data.dto.PaymentMethodDto
import com.example.nusamart.data.dto.UpdatePaymentStatusRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PaymentApi {

    // GET /api/payments/methods
    @GET("payments/methods")
    suspend fun getPaymentMethods(): List<PaymentMethodDto>

    // GET /api/payments/{id}
    @GET("payments/{id}")
    suspend fun getPaymentById(@Path("id") id: String): PaymentDto

    // GET /api/payments/order/{orderId}
    @GET("payments/order/{orderId}")
    suspend fun getPaymentByOrderId(@Path("orderId") orderId: String): PaymentDto

    // POST /api/payments
    @POST("payments")
    suspend fun createPayment(@Body request: CreatePaymentRequest): PaymentActionResponse

    // PUT /api/payments/{id}/status
    @PUT("payments/{id}/status")
    suspend fun updatePaymentStatus(
        @Path("id") id: String,
        @Body request: UpdatePaymentStatusRequest
    ): PaymentActionResponse
}