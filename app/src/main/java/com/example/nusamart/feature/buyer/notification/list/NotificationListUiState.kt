package com.example.nusamart.feature.buyer.notification.list

import com.example.nusamart.data.dto.NotificationDto

data class NotificationListUiState(
    val isLoading: Boolean = true,
    val notifications: List<NotificationDto> = emptyList()
)