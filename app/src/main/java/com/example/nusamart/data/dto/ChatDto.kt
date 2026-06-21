package com.example.nusamart.data.dto

import com.google.gson.annotations.SerializedName

// RESPONSE
data class RoomChatDto(
    @SerializedName("idRoom") val idRoom: String,
    @SerializedName("idUser1") val idUser1: String,
    @SerializedName("idUser2") val idUser2: String,
    @SerializedName("lastMessage") val lastMessage: String?,
    @SerializedName("createAt") val createAt: String,
    @SerializedName("updateAt") val updateAt: String
)

data class ChatMessageDto(
    @SerializedName("idChat") val idChat: String,
    @SerializedName("idRoom") val idRoom: String,
    @SerializedName("senderId") val senderId: String,
    @SerializedName("messageText") val messageText: String,
    @SerializedName("isRead") val isRead: Int,
    @SerializedName("createAt") val createAt: String
)

data class ChatActionResponse(
    @SerializedName("message") val message: String,
    @SerializedName("chat") val chat: ChatMessageDto?
)


// REQUEST

data class GetOrCreateRoomRequest(
    @SerializedName("idUser2") val idUser2: String
)

data class SendMessageRequest(
    @SerializedName("messageText") val messageText: String
)