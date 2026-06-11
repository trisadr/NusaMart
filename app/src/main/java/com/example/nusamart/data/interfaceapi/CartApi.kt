package com.example.nusamart.data.interfaceapi

import com.example.nusamart.data.dto.AddCartItemRequest
import com.example.nusamart.data.dto.CartActionResponse
import com.example.nusamart.data.dto.CartResponse
import com.example.nusamart.data.dto.GeneralResponse
import com.example.nusamart.data.dto.UpdateCheckedRequest
import com.example.nusamart.data.dto.UpdateQuantityRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApi {

    @GET("cart")
    suspend fun getCart(): CartResponse

    @POST("cart/items")
    suspend fun addItem(@Body request: AddCartItemRequest): CartActionResponse

    @PUT("cart/items/{id}/quantity")
    suspend fun updateQuantity(@Path("id") id: String, @Body request: UpdateQuantityRequest): CartActionResponse

    @PUT("cart/items/{id}/checked")
    suspend fun updateChecked(@Path("id") id: String, @Body request: UpdateCheckedRequest): CartActionResponse

    @PUT("cart/check-all")
    suspend fun updateAllChecked(@Body request: UpdateCheckedRequest): GeneralResponse

    @DELETE("cart/items/{id}")
    suspend fun deleteItem(@Path("id") id: String): GeneralResponse
}