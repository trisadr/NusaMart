package com.example.nusamart.data.model.user

data class User(
    val idUser: String,
    val username: String,
    val email: String,
    val passwordHashed: String,
    val phone: String,
    val role: Role,
    val createAt: java.time.LocalDateTime,
    val updateAt: java.time.LocalDateTime,
    val imageURL: Int? = null
) {
    enum class Role {
        BUYER,
        SELLER,
        ADMIN
    }
}
