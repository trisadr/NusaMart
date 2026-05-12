package com.example.nusamart.data.model.chat

data class Chat(
    val idChat: String,             // PK
    val idRoom: String,             // FK (RoomChat)
    val senderId: String,           // FK (User)
    val messageText: String,
    val isRead: Boolean,
    val createAt: java.time.LocalDateTime
)
