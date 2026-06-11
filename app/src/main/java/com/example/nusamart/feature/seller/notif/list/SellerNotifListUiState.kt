package com.example.nusamart.feature.seller.notif.list

import com.example.nusamart.data.dto.NotificationDto

data class SellerNotifListUiState(
    val isLoading: Boolean = true,
    val notifications: List<NotificationDto> = emptyList()
)