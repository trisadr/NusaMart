package com.example.nusamart.feature.buyer.notification.detail

import com.example.nusamart.data.repository.notif.NotificationJson

data class NotificationDetailUiState(
    val isLoading: Boolean = true,
    val notification: NotificationJson? = null
)