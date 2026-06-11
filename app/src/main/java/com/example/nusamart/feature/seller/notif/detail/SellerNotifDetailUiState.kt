package com.example.nusamart.feature.seller.notif.detail

import com.example.nusamart.data.dto.NotificationDto

data class SellerNotifDetailUiState(
    val isLoading: Boolean = true,
    val notification: NotificationDto? = null
)