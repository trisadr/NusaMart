package com.example.nusamart.feature.chat.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.repository.chat.ChatRepository
import com.example.nusamart.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatDetailVM @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState = _uiState.asStateFlow()

    private var currentRoomId: String = ""
    private var myId: String = ""

    fun loadChat(roomId: String) = viewModelScope.launch {
        currentRoomId = roomId
        _uiState.update { it.copy(isLoading = true) }

        myId = userRepository.getActiveUserId() ?: ""

        val room = chatRepository.getRoomById(roomId)
        val otherUserId = if (room?.idUser1 == myId) room?.idUser2 else room?.idUser1
        val otherUser = otherUserId?.let { userRepository.getUserById(it) }

        if (myId.isNotBlank()) {
            chatRepository.markMessagesAsRead(roomId)
        }

        val messages = chatRepository.getChatsByRoom(roomId)

        _uiState.update {
            it.copy(
                isLoading = false,
                messages = messages,
                currentUserId = myId,
                otherUserName = otherUser?.username ?: "Chat",
                otherUserImageUrl = otherUser?.imageURL
            )
        }
    }

    fun sendMessage(text: String) = viewModelScope.launch {
        if (myId.isBlank() || currentRoomId.isBlank()) return@launch
        val trimmed = text.trim()
        if (trimmed.isBlank()) return@launch

        chatRepository.sendMessage(currentRoomId, text)
        val messages = chatRepository.getChatsByRoom(currentRoomId)
        _uiState.update { it.copy(messages = messages) }
    }
}