package com.example.nusamart.feature.buyer.notification.list

import com.example.nusamart.data.repository.notif.NotificationJson

// --- STATE ---
data class NotificationListUiState(
    val isLoading: Boolean = true,
    val notifications: List<NotificationJson> = emptyList()
)