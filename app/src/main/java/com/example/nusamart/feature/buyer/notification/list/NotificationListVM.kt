package com.example.nusamart.feature.buyer.notification.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.core.MyApplication
import com.example.nusamart.data.repository.notif.NotificationRepository
import com.example.nusamart.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationListVM(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                NotificationListVM(app.notificationRepository, app.userRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(NotificationListUiState())
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
            loadNotifications() // Reload data
        }
    }
}