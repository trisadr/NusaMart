package com.example.nusamart.data.model.review

data class ReviewImage(
    val idRevImage: String,         // PK
    val idReview: String,           // FK (Review)
    val urlImage: Int            // corrected from Int -> String
)
