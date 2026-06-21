package com.example.nusamart.feature.chat.list

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
class ChatListVM @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState = _uiState.asStateFlow()

    fun loadChatRooms() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        val myId = userRepository.getActiveUserId()

        if (myId == null) {
            _uiState.update { it.copy(isLoading = false, chatRooms = emptyList()) }
            return@launch
        }

        val rooms = chatRepository.getChatRooms()
        val uiModels = rooms.map { room ->
            val partnerId = if (room.idUser1 == myId) room.idUser2 else room.idUser1
            val partner = userRepository.getUserById(partnerId)

            ChatRoomUiModel(
                roomId = room.idRoom,
                partnerName = partner?.username ?: "Pengguna NusaMart",
                partnerImageUrl = partner?.imageURL,
                lastMessage = room.lastMessage?.takeIf { it.isNotBlank() }
                    ?: "Mulai percakapan...",
                updatedAt = room.updateAt.toString()
            )
        }

        _uiState.update { it.copy(chatRooms = uiModels, isLoading = false) }
    }
}