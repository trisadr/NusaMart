package com.example.nusamart.feature.chat.detail

import com.example.nusamart.data.model.chat.Chat

data class ChatDetailUiState(
    val isLoading: Boolean = true,
    val messages: List<Chat> = emptyList(),
    val currentUserId: String = "",
    val otherUserName: String = "",
    val otherUserImageUrl: String? = null
)