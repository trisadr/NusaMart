package com.example.nusamart.data.model.notif

data class Notification(
    val idNotif: String,            // PK
    val idUser: String,             // FK (User)
    val title: String,
    val body: String,
    val type: NotifType,
    val isRead: Boolean,
    val createAt: java.time.LocalDateTime,
    val referenceId: String? = null,
    val referenceType: ReferenceType? = null
) {
    enum class NotifType {
        ORDER,
        SISTEM
    }

    enum class ReferenceType {
        ORDER,
        PAYMENT,
        SYSTEM
    }
}
