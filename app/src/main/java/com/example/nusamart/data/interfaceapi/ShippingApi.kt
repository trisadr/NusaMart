package com.example.nusamart.data.interfaceapi

import com.example.nusamart.data.dto.AddTrackingRequest
import com.example.nusamart.data.dto.CourierOptionDto
import com.example.nusamart.data.dto.CreateShippingRequest
import com.example.nusamart.data.dto.ShippingActionResponse
import com.example.nusamart.data.dto.ShippingDto
import com.example.nusamart.data.dto.ShippingTrackingDto
import com.example.nusamart.data.dto.TrackingActionResponse
import com.example.nusamart.data.dto.UpdateShippingStatusRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ShippingApi {

    // PUBLIC
    @GET("couriers")
    suspend fun getCouriers(): List<CourierOptionDto>

    @GET("couriers/{id}")
    suspend fun getCourierDetail(@Path("id") id: String): CourierOptionDto

    // PROTECTED BUYER/SELLER
    @GET("shipping/order/{orderId}")
    suspend fun getShippingByOrder(@Path("orderId") orderId: String): ShippingDto

    @GET("shipping/{id}/tracking")
    suspend fun getTrackingHistory(@Path("id") id: String): List<ShippingTrackingDto>

    // SELLER ONLY
    @POST("seller/shipping")
    suspend fun createShipping(@Body request: CreateShippingRequest): ShippingActionResponse

    @PUT("seller/shipping/{id}/status")
    suspend fun updateShippingStatus(@Path("id") id: String, @Body request: UpdateShippingStatusRequest): ShippingActionResponse

    @POST("seller/shipping/{id}/tracking")
    suspend fun addTrackingUpdate(@Path("id") id: String, @Body request: AddTrackingRequest): TrackingActionResponse
}