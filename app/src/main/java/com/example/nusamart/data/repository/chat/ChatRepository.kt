package com.example.nusamart.data.repository.chat

import android.content.Context
import com.example.nusamart.data.model.chat.Chat
import com.example.nusamart.data.model.chat.RoomChat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken   // ← TAMBAH import ini
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDateTime::class.java, JsonSerializer<LocalDateTime> { src, _, _ ->
            com.google.gson.JsonPrimitive(src.format(formatter))
        })
        .registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer { json, _, _ ->
            LocalDateTime.parse(json.asString, formatter)
        })
        .create()

    private val roomFile = "room_chat.json"
    private val chatFile = "chat.json"

    // PERBAIKAN 1 & 2: gunakan TypeToken (bukan Array<T>::class.java)
    // + tambah fallback ke assets seperti StoreRepository
    private inline fun <reified T> readJson(fileName: String): MutableList<T> {
        val file = File(context.filesDir, fileName)

        // PERBAIKAN 1: fallback ke assets jika file belum ada
        if (!file.exists()) {
            try {
                context.assets.open(fileName).use { inputStream ->
                    file.writeText(inputStream.bufferedReader().readText())
                }
            } catch (e: Exception) {
                // File tidak ada di assets juga → return kosong (wajar untuk chat)
                return mutableListOf()
            }
        }

        val json = file.readText()
        if (json.isBlank()) return mutableListOf()

        // PERBAIKAN 2: gunakan TypeToken agar generics bisa di-parse dengan benar
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson<List<T>>(json, type)?.toMutableList() ?: mutableListOf()
    }

    private fun <T> writeJson(fileName: String, data: List<T>) {
        val file = File(context.filesDir, fileName)
        file.writeText(gson.toJson(data))
    }

    // --- ROOM CHAT ---

    suspend fun getChatRooms(userId: String): List<RoomChat> = withContext(Dispatchers.IO) {
        readJson<RoomChat>(roomFile)
            .filter { it.idUser1 == userId || it.idUser2 == userId }
            .sortedByDescending { it.updateAt }
    }

    suspend fun getRoomById(roomId: String): RoomChat? = withContext(Dispatchers.IO) {
        readJson<RoomChat>(roomFile).find { it.idRoom == roomId }
    }

    suspend fun getOrCreateRoom(user1: String, user2: String): RoomChat = withContext(Dispatchers.IO) {
        val rooms = readJson<RoomChat>(roomFile)
        val existing = rooms.find {
            (it.idUser1 == user1 && it.idUser2 == user2) ||
                    (it.idUser1 == user2 && it.idUser2 == user1)
        }
        if (existing != null) return@withContext existing

        val newRoom = RoomChat(
            idRoom = "ROOM-${System.currentTimeMillis()}",
            idUser1 = user1,
            idUser2 = user2,
            createAt = LocalDateTime.now(),
            updateAt = LocalDateTime.now()
        )
        rooms.add(newRoom)
        writeJson(roomFile, rooms)
        return@withContext newRoom
    }

    // --- CHAT DETAIL ---

    suspend fun getChatsByRoom(roomId: String): List<Chat> = withContext(Dispatchers.IO) {
        readJson<Chat>(chatFile)
            .filter { it.idRoom == roomId }
            .sortedBy { it.createAt }
    }

    suspend fun sendMessage(roomId: String, senderId: String, text: String) = withContext(Dispatchers.IO) {
        val chats = readJson<Chat>(chatFile)
        val newChat = Chat(
            idChat = "MSG-${System.currentTimeMillis()}",
            idRoom = roomId,
            senderId = senderId,
            messageText = text,
            isRead = false,
            createAt = LocalDateTime.now()
        )
        chats.add(newChat)
        writeJson(chatFile, chats)

        val rooms = readJson<RoomChat>(roomFile)
        val idx = rooms.indexOfFirst { it.idRoom == roomId }
        if (idx != -1) {
            rooms[idx] = rooms[idx].copy(lastMessage = text, updateAt = LocalDateTime.now())
            writeJson(roomFile, rooms)
        }
    }

    suspend fun markMessagesAsRead(roomId: String, currentUserId: String) = withContext(Dispatchers.IO) {
        val chats = readJson<Chat>(chatFile)
        var hasChanges = false

        val updated = chats.map { chat ->
            // Tandai pesan yang bukan dari saya sebagai sudah dibaca
            if (chat.idRoom == roomId && chat.senderId != currentUserId && !chat.isRead) {
                hasChanges = true
                chat.copy(isRead = true)
            } else {
                chat
            }
        }

        if (hasChanges) writeJson(chatFile, updated)
    }
}