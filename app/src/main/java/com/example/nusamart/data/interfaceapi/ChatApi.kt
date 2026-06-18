package com.example.nusamart.data.interfaceapi

import com.example.nusamart.data.dto.ChatActionResponse
import com.example.nusamart.data.dto.ChatMessageDto
import com.example.nusamart.data.dto.GeneralResponse
import com.example.nusamart.data.dto.GetOrCreateRoomRequest
import com.example.nusamart.data.dto.RoomChatDto
import com.example.nusamart.data.dto.SendMessageRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ChatApi {

    @GET("chat/rooms")
    suspend fun getChatRooms(): List<RoomChatDto>

    @GET("chat/rooms/{id}")
    suspend fun getRoomDetail(@Path("id") roomId: String): RoomChatDto

    @POST("chat/rooms")
    suspend fun getOrCreateRoom(@Body request: GetOrCreateRoomRequest): RoomChatDto

    @GET("chat/rooms/{id}/messages")
    suspend fun getMessages(@Path("id") roomId: String): List<ChatMessageDto>

    @POST("chat/rooms/{id}/messages")
    suspend fun sendMessage(
        @Path("id") roomId: String,
        @Body request: SendMessageRequest
    ): ChatActionResponse

    @PUT("chat/rooms/{id}/read")
    suspend fun markAsRead(@Path("id") roomId: String): GeneralResponse
}