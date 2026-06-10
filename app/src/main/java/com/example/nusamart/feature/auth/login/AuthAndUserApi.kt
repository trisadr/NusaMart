package com.example.nusamart.feature.auth.login

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

    // ── AUTHENTICATION ─────────────────────────────────────
    @POST("auth/login")
    suspend fun login(
        @Body request: Map<String, String> // Kirim ["email": "...", "password": "..."]
    ): Response<AuthResponse>

    // ── USER PROFILE ───────────────────────────────────────
    @GET("user/profile")
    suspend fun getProfile(): Response<UserProfileResponse>

    @PUT("user/profile")
    suspend fun updateProfile(
        @Body data: Map<String, String>
    ): Response<CommonResponse<UserProfileResponse>>

    // ── ADDRESSES ──────────────────────────────────────────
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

    // Tambahkan ini di dalam interface AuthAndUserApi kamu:

    @POST("auth/register")
    suspend fun register(
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): retrofit2.Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(): retrofit2.Response<Map<String, String>>

    // ── MENGAMBIL USER BERDASARKAN ID ──────────────────────
    @GET("user/{id}") // Sesuaikan dengan route di backend Laravel-mu
    suspend fun getUserById(
        @Path("id") userId: String
    ): Response<UserProfileResponse> // Asumsi response-nya sama dengan UserProfileResponse
}