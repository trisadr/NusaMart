package com.example.nusamart.data.model.user

import java.time.LocalDateTime

data class User(
    val idUser: String,
    val username: String,
    val email: String,
    val password: String, // Sementara password biasa (tanpa hash)
    val phone: String,
    val role: Role,
    val createAt: LocalDateTime,
    val updateAt: LocalDateTime,
    val imageURL: String? = null
) {
    enum class Role {
        BUYER,
        SELLER,
        ADMIN
    }
}