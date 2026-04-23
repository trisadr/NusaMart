package com.example.nusamart.entity

data class Notification(
    val idNotification: String,
    val idOrder: String,
    val idStore: String,
    val message: String,
    // Field 'type' dihapus sesuai permintaan
    val isRead: Boolean
)