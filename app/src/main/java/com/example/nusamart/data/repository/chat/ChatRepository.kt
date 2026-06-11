package com.example.nusamart.data.repository.chat

import com.example.nusamart.data.dto.ChatMessageDto
import com.example.nusamart.data.dto.GetOrCreateRoomRequest
import com.example.nusamart.data.dto.RoomChatDto
import com.example.nusamart.data.dto.SendMessageRequest
import com.example.nusamart.data.interfaceapi.ChatApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val apiService: ChatApi
) {

    // --- ROOM CHAT ---

    suspend fun getChatRooms(): List<RoomChatDto> = withContext(Dispatchers.IO) {
        try {
            apiService.getChatRooms()
        } catch (e: Exception) {
            emptyList() // Kembalikan list kosong jika error/belum ada chat
        }
    }

    suspend fun getRoomById(roomId: String): RoomChatDto? = withContext(Dispatchers.IO) {
        try {
            apiService.getRoomDetail(roomId)
        } catch (e: Exception) {
            null
        }
    }

    // Perhatikan: Kita hanya perlu mengirim ID lawan bicara, Laravel akan otomatis
    // mencari tahu ID kita dari token Bearer yang dikirim Retrofit.
    suspend fun getOrCreateRoom(idUser2: String): RoomChatDto? = withContext(Dispatchers.IO) {
        try {
            val request = GetOrCreateRoomRequest(idUser2)
            apiService.getOrCreateRoom(request)
        } catch (e: Exception) {
            null
        }
    }


    // --- CHAT MESSAGES ---

    suspend fun getChatsByRoom(roomId: String): List<ChatMessageDto> = withContext(Dispatchers.IO) {
        try {
            apiService.getMessages(roomId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun sendMessage(roomId: String, text: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = SendMessageRequest(text)
            apiService.sendMessage(roomId, request)
            true // Berhasil
        } catch (e: Exception) {
            false // Gagal
        }
    }

    suspend fun markMessagesAsRead(roomId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            apiService.markAsRead(roomId)
            true
        } catch (e: Exception) {
            false
        }
    }
}