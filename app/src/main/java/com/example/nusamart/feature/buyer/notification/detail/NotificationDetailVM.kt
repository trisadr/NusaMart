package com.example.nusamart.feature.buyer.notification.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.core.MyApplication
import com.example.nusamart.data.repository.notif.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- VIEWMODEL ---
class NotificationDetailVM(
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                NotificationDetailVM(app.notificationRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(NotificationDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadAndMarkAsRead(notifId: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        // Tandai dibaca dulu
        notificationRepository.markAsRead(notifId)

        // Tarik data
        val notif = notificationRepository.getNotificationById(notifId)
        _uiState.update { it.copy(isLoading = false, notification = notif) }
    }
}