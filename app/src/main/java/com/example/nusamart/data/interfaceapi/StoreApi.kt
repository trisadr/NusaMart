package com.example.nusamart.data.interfaceapi

import com.example.nusamart.data.dto.StoreDto
import com.example.nusamart.data.dto.UpdateStoreResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface StoreApi {

    // Public
    @GET("stores/{id}")
    suspend fun getStoreById(@Path("id") storeId: String): Response<StoreDto>

    // Protected
    @GET("seller/store")
    suspend fun getMyStore(): Response<StoreDto>

    // Protected
    @PUT("seller/store")
    suspend fun updateStore(@Body request: Map<String, String?>): Response<UpdateStoreResponse>

    @GET("stores")
    suspend fun getAllStores(): Response<List<StoreDto>>
}