package com.example.nusamart.feature.buyer.notification.detail

import com.example.nusamart.data.dto.NotificationDto

data class NotificationDetailUiState(
    val isLoading: Boolean = true,
    val notification: NotificationDto? = null
)