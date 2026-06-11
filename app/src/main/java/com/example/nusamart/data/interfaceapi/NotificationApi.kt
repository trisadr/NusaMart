package com.example.nusamart.data.interfaceapi

import com.example.nusamart.data.dto.GeneralResponse // Gunakan dari file sebelumnya (ex: CartDto/ReviewDto)
import com.example.nusamart.data.dto.NotificationActionResponse
import com.example.nusamart.data.dto.NotificationDto
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificationApi {

    @GET("notifications")
    suspend fun getNotifications(): List<NotificationDto>

    @GET("notifications/{id}")
    suspend fun getNotificationDetail(@Path("id") id: String): NotificationDto

    @PUT("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: String): NotificationActionResponse

    @PUT("notifications/read-all")
    suspend fun markAllAsRead(): GeneralResponse
}