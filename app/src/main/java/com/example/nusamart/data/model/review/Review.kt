package com.example.nusamart.data.model.review

data class Review(
    val idReview: String,           // PK
    val idOrderItem: String,        // FK (OrderItem)
    val idUser: String,             // FK (User)
    val rating: Double,
    val comment: String? = null,
    val isHidden: Boolean,
    val createAt: java.time.LocalDateTime
)
