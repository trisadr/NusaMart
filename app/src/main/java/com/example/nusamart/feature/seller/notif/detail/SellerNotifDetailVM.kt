package com.example.nusamart.feature.seller.notif.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.repository.notif.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerNotifDetailVM @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SellerNotifDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadAndMarkAsRead(notifId: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        notificationRepository.markAsRead(notifId)

        val notif = notificationRepository.getNotificationById(notifId)
        _uiState.update { it.copy(isLoading = false, notification = notif) }
    }
}