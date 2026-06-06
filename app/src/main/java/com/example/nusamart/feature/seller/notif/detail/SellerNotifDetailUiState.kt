package com.example.nusamart.feature.seller.notif.detail

import com.example.nusamart.data.repository.notif.NotificationJson

data class SellerNotifDetailUiState(
    val isLoading: Boolean = true,
    val notification: NotificationJson? = null
)