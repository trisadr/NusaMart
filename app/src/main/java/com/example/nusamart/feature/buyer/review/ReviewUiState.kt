package com.example.nusamart.feature.buyer.review

data class ReviewItemForm(
    val idOrderItem: String,
    val productName: String,
    val quantity: Int,
    val productImageResId: Int,
    val rating: Int = 0,
    val comment: String = "",
    val selectedPhotoResId: Int? = null
)

data class ReviewUiState(
    val isLoading: Boolean = true,
    val orderId: String = "",
    val itemsToReview: List<ReviewItemForm> = emptyList(),
    val showValidationError: Boolean = false,
    val isSubmitSuccess: Boolean = false,
    val isOrderDelivered: Boolean = true,     // validasi untuk menentukan apakah bisa isi review
    val allReviewed: Boolean = false
)