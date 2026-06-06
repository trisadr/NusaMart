package com.example.nusamart.feature.seller.notif.list

import com.example.nusamart.data.repository.notif.NotificationJson

data class SellerNotifListUiState(
    val isLoading: Boolean = true,
    val notifications: List<NotificationJson> = emptyList()
)