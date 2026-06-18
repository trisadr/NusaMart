package com.example.nusamart.data.dto

import com.google.gson.annotations.SerializedName

// RESPONSE (Balasan dari Server)

data class ReviewDto(
    @SerializedName("idReview") val idReview: String,
    @SerializedName("idOrderItem") val idOrderItem: String,
    @SerializedName("idUser") val idUser: String,
    @SerializedName("rating") val rating: Double,
    @SerializedName("comment") val comment: String?,
    @SerializedName("isHidden") val isHidden: Int,
    @SerializedName("createAt") val createAt: String,

    // Hasil Eager Loading (with('reviewImages'))
    @SerializedName("review_images") val reviewImages: List<ReviewImageDto>? = null
)

data class ReviewImageDto(
    @SerializedName("idRevImage") val idRevImage: String,
    @SerializedName("idReview") val idReview: String,
    @SerializedName("urlImage") val urlImage: String?
)

data class ReviewActionResponse(
    @SerializedName("message") val message: String,
    @SerializedName("review") val review: ReviewDto?
)


// REQUEST (Data yang dikirim ke Server)
data class ByItemsRequest(
    @SerializedName("itemIds") val itemIds: List<String>
)