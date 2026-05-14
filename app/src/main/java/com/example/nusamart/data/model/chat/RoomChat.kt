package com.example.nusamart.data.model.chat

data class RoomChat(
    val idRoom: String,             // PK
    val idUser1: String,            // FK (User) - buyer
    val idUser2: String,            // FK (User) - seller
    val lastMessage: String? = null,
    val createAt: java.time.LocalDateTime,
    val updateAt: java.time.LocalDateTime
)
