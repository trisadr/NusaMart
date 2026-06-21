package com.example.nusamart.data.interfaceapi

import com.example.nusamart.data.dto.AddressDto
import com.example.nusamart.data.dto.AuthResponse
import com.example.nusamart.data.dto.CommonResponse
import com.example.nusamart.data.dto.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface AuthAndUserApi {

    // Auth
    @POST("auth/login")
    suspend fun login(
        @Body request: Map<String, String> // Kirim ["email": "...", "password": "..."]
    ): Response<AuthResponse>

    // User profile
    @GET("user/profile")
    suspend fun getProfile(): Response<UserProfileResponse>

    @PUT("user/profile")
    suspend fun updateProfile(
        @Body data: Map<String, String>
    ): Response<CommonResponse<UserProfileResponse>>

    // Address
    @GET("user/addresses")
    suspend fun getAddresses(): Response<List<AddressDto>>

    @POST("user/addresses")
    suspend fun addAddress(
        @Body address: Map<String, Any>
    ): Response<CommonResponse<AddressDto>>

    @PUT("user/addresses/{id}")
    suspend fun updateAddress(
        @Path("id") id: String,
        @Body address: Map<String, Any>
    ): Response<CommonResponse<AddressDto>>

    @DELETE("user/addresses/{id}")
    suspend fun deleteAddress(
        @Path("id") id: String
    ): Response<CommonResponse<Unit>>

    @PUT("user/addresses/{id}/default")
    suspend fun setDefaultAddress(
        @Path("id") id: String
    ): Response<CommonResponse<AddressDto>>

    @POST("auth/register")
    suspend fun register(
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<Map<String, String>>

    @GET("user/{id}")
    suspend fun getUserById(
        @Path("id") userId: String
    ): Response<UserProfileResponse>
}