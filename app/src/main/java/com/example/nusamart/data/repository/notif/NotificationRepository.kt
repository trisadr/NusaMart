package com.example.nusamart.data.repository.notif

import com.example.nusamart.data.dto.NotificationDto
import com.example.nusamart.data.interfaceapi.NotificationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val apiService: NotificationApi
) {

    // Mengambil semua notifikasi milik user yang sedang login
    suspend fun getNotificationsByUser(): List<NotificationDto> = withContext(Dispatchers.IO) {
        try {
            // Kita tidak perlu mengirim userId sebagai parameter,
            // karena Laravel sudah membacanya otomatis dari Token Sanctum (Bearer)
            apiService.getNotifications()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Mengambil detail satu notifikasi
    suspend fun getNotificationById(notifId: String): NotificationDto? = withContext(Dispatchers.IO) {
        try {
            apiService.getNotificationDetail(notifId)
        } catch (e: Exception) {
            null
        }
    }

    // Menandai satu notifikasi sebagai "sudah dibaca"
    suspend fun markAsRead(notifId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            apiService.markAsRead(notifId)
            true
        } catch (e: Exception) {
            false
        }
    }

    // Menandai seluruh notifikasi sebagai "sudah dibaca"
    suspend fun markAllAsRead(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Sama seperti get, kita tidak perlu mengirim userId, Laravel otomatis memproses user saat ini
            apiService.markAllAsRead()
            true
        } catch (e: Exception) {
            false
        }
    }
}