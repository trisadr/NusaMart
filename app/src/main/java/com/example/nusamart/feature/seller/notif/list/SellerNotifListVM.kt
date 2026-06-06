package com.example.nusamart.feature.seller.notif.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.repository.notif.NotificationRepository
import com.example.nusamart.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerNotifListVM @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SellerNotifListUiState())
    val uiState = _uiState.asStateFlow()

    fun loadNotifications() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        val userId = userRepository.getActiveUserId()
        if (userId != null) {
            val list = notificationRepository.getNotificationsByUser(userId)
            _uiState.update { it.copy(isLoading = false, notifications = list) }
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun markAllAsRead() = viewModelScope.launch {
        val userId = userRepository.getActiveUserId()
        if (userId != null) {
            notificationRepository.markAllAsRead(userId)
            loadNotifications()
        }
    }
}