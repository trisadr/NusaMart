package com.example.nusamart.feature.chat.list

data class ChatListUiState(
    val isLoading: Boolean = true,
    val chatRooms: List<ChatRoomUiModel> = emptyList()
)

data class ChatRoomUiModel(
    val roomId: String,
    val partnerName: String,
    val partnerImageUrl: String? = null,
    val lastMessage: String,
    val updatedAt: String
)