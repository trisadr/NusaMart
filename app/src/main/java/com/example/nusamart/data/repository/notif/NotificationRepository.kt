package com.example.nusamart.data.repository.notif

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

// JSON-Friendly Models
data class NotificationJson(
    val idNotif: String,
    val idUser: String,
    val title: String,
    val body: String,
    val type: String,
    val isRead: Boolean,
    val createAt: String,
    val referenceId: String? = null,
    val referenceType: String? = null
)

@Singleton
class NotificationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val notifFile = "notification.json"

    // Fungsi baca internal yang aman dari crash (FilesDir prioritized)
    private fun readNotifJson(): MutableList<NotificationJson> {
        val file = File(context.filesDir, notifFile)

        if (!file.exists()) {
            try {
                context.assets.open(notifFile).use { inputStream ->
                    file.writeText(inputStream.bufferedReader().readText())
                }
            } catch (e: Exception) {
                return mutableListOf()
            }
        }

        return try {
            val json = file.readText()
            if (json.isBlank()) return mutableListOf()
            val array = gson.fromJson(json, Array<NotificationJson>::class.java)
            array?.toMutableList() ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    private fun writeJson(data: List<NotificationJson>) {
        try {
            // Dipastikan menulis ke filesDir yang memiliki akses write penuh
            val file = File(context.filesDir, notifFile)
            file.writeText(gson.toJson(data))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // FUNGSI BARU: Dipanggil oleh UserRepository saat registrasi berhasil
    suspend fun addSystemNotification(userId: String, username: String, isSeller: Boolean) = withContext(Dispatchers.IO) {
        try {
            val notifications = readNotifJson()
            val notifId = "NTF-${System.currentTimeMillis()}"
            val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            val userTypeStr = if (isSeller) "Toko" else "Akun"

            val newNotif = NotificationJson(
                idNotif = notifId,
                idUser = userId,
                title = "Selamat Datang di NusaMart!",
                body = "Halo $username, $userTypeStr kamu berhasil dibuat. Jangan lupa cek email kamu untuk info lebih lanjut ya!",
                type = "SISTEM",
                isRead = false,
                createAt = now,
                referenceId = null,
                referenceType = null
            )

            notifications.add(newNotif)
            writeJson(notifications)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getNotificationsByUser(userId: String): List<NotificationJson> = withContext(Dispatchers.IO) {
        val notifications = readNotifJson()
        return@withContext notifications.filter { it.idUser == userId }
            .sortedByDescending { it.createAt }
    }

    suspend fun getNotificationById(notifId: String): NotificationJson? = withContext(Dispatchers.IO) {
        val notifications = readNotifJson()
        return@withContext notifications.find { it.idNotif == notifId }
    }

    suspend fun markAsRead(notifId: String) = withContext(Dispatchers.IO) {
        val notifications = readNotifJson()
        val index = notifications.indexOfFirst { it.idNotif == notifId }
        if (index != -1 && !notifications[index].isRead) {
            notifications[index] = notifications[index].copy(isRead = true)
            writeJson(notifications)
        }
    }

    suspend fun markAllAsRead(userId: String) = withContext(Dispatchers.IO) {
        val notifications = readNotifJson()
        var isUpdated = false
        for (i in notifications.indices) {
            if (notifications[i].idUser == userId && !notifications[i].isRead) {
                notifications[i] = notifications[i].copy(isRead = true)
                isUpdated = true
            }
        }
        if (isUpdated) {
            writeJson(notifications)
        }
    }
}