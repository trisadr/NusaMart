package com.example.nusamart.data.dto

import com.google.gson.annotations.SerializedName

// --- RESPONSE (Balasan dari Server) ---

data class NotificationDto(
    @SerializedName("idNotif") val idNotif: String,
    @SerializedName("idUser") val idUser: String,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("type") val type: String,
    @SerializedName("isRead") val isRead: Int, // Gunakan Int (0/1) untuk konsistensi dengan MySQL
    @SerializedName("createAt") val createAt: String,
    @SerializedName("referenceId") val referenceId: String?,
    @SerializedName("referenceType") val referenceType: String?
)

data class NotificationActionResponse(
    @SerializedName("message") val message: String,
    @SerializedName("notification") val notification: NotificationDto?
)