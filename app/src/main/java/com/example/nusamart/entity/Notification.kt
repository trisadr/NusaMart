package com.example.nusamart.entity

data class Notification(
    val idNotification: String,
    val idOrder: String?,
    val idStore: String?,
    val title: String,
    val message: String,
    val date: String,
    val type: String,
    val isRead: Boolean
)