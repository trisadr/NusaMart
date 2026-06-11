package com.example.nusamart.data.interfaceapi

import com.example.nusamart.data.dto.CreateOrderRequest
import com.example.nusamart.data.dto.IsReviewedResponse
import com.example.nusamart.data.dto.OrderActionResponse
import com.example.nusamart.data.dto.OrderDto
import com.example.nusamart.data.dto.UpdateOrderStatusRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OrderApi {

    // GET /api/orders
    @GET("orders")
    suspend fun getOrders(): List<OrderDto>

    // GET /api/orders/{id}
    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): OrderDto

    // POST /api/orders
    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): OrderActionResponse

    // PUT /api/orders/{id}/cancel
    @PUT("orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") id: String): OrderActionResponse

    // GET /api/orders/{id}/reviewed
    @GET("orders/{id}/reviewed")
    suspend fun isReviewed(@Path("id") id: String): IsReviewedResponse

    // GET /api/seller/orders
    @GET("seller/orders")
    suspend fun getSellerOrders(): List<OrderDto>

    // PUT /api/seller/orders/{id}/status
    @PUT("seller/orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: String,
        @Body request: UpdateOrderStatusRequest
    ): OrderActionResponse
}