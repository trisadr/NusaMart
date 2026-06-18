package com.example.nusamart.data.interfaceapi

import com.example.nusamart.data.dto.ByItemsRequest
import com.example.nusamart.data.dto.GeneralResponse
import com.example.nusamart.data.dto.ReviewActionResponse
import com.example.nusamart.data.dto.ReviewDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ReviewApi {

    @GET("reviews/product/{productId}")
    suspend fun getReviewsByProduct(@Path("productId") productId: String): List<ReviewDto>

    @POST("reviews/items")
    suspend fun getReviewsByItems(@Body request: ByItemsRequest): List<ReviewDto>

    @Multipart
    @POST("reviews")
    suspend fun createReview(
        @Part("idOrderItem") idOrderItem: RequestBody,
        @Part("rating") rating: RequestBody,
        @Part("comment") comment: RequestBody?,
        @Part imageFile: MultipartBody.Part?
    ): ReviewActionResponse

    @PUT("admin/reviews/{id}/hide")
    suspend fun hideReview(@Path("id") id: String): GeneralResponse
}