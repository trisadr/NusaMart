package com.example.nusamart.data.repository.notif

import android.content.Context
import com.example.nusamart.data.model.notif.Notification
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// ─── JSON-Friendly Models ─────────────────────────────────────────────────────

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

// ─── Repository ──────────────────────────────────────────────────────────────

class NotificationRepository(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val notifFile = "notification.json"

    private inline fun <reified T> readJson(fileName: String): MutableList<T> {
        val file = File(context.filesDir, fileName)

        if (!file.exists()) {
            try {
                context.assets.open(fileName).use { inputStream ->
                    val json = inputStream.bufferedReader().readText()
                    file.writeText(json)
                }
            } catch (e: Exception) {
                return mutableListOf()
            }
        }

        val json = file.readText()
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    private fun <T> writeJson(fileName: String, data: List<T>) {
        val file = File(context.filesDir, fileName)
        file.writeText(gson.toJson(data))
    }

    suspend fun getNotificationsByUser(userId: String): List<NotificationJson> = withContext(Dispatchers.IO) {
        val notifications = readJson<NotificationJson>(notifFile)
        // Urutkan dari yang terbaru
        return@withContext notifications.filter { it.idUser == userId }
            .sortedByDescending { it.createAt }
    }

    suspend fun getNotificationById(notifId: String): NotificationJson? = withContext(Dispatchers.IO) {
        val notifications = readJson<NotificationJson>(notifFile)
        return@withContext notifications.find { it.idNotif == notifId }
    }

    suspend fun markAsRead(notifId: String) = withContext(Dispatchers.IO) {
        val notifications = readJson<NotificationJson>(notifFile)
        val index = notifications.indexOfFirst { it.idNotif == notifId }
        if (index != -1 && !notifications[index].isRead) {
            notifications[index] = notifications[index].copy(isRead = true)
            writeJson(notifFile, notifications)
        }
    }

    suspend fun markAllAsRead(userId: String) = withContext(Dispatchers.IO) {
        val notifications = readJson<NotificationJson>(notifFile)
        var isUpdated = false
        for (i in notifications.indices) {
            if (notifications[i].idUser == userId && !notifications[i].isRead) {
                notifications[i] = notifications[i].copy(isRead = true)
                isUpdated = true
            }
        }
        if (isUpdated) {
            writeJson(notifFile, notifications)
        }
    }
}