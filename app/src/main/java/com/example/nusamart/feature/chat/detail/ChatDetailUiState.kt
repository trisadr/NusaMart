package com.example.nusamart.feature.chat.detail

import com.example.nusamart.data.dto.ChatMessageDto

data class ChatDetailUiState(
    val isLoading: Boolean = true,
    val messages: List<ChatMessageDto> = emptyList(),
    val currentUserId: String = "",
    val otherUserName: String = "",
    val otherUserImageUrl: String? = null
)