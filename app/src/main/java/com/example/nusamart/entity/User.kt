package com.example.nusamart.entity

abstract class User(
    open val email: String,
    open val username: String,
    open val password: String, // Catatan: Sebaiknya jangan simpan plain password di production
    open val address: String, // Sementara tunggal
    open val profilePicResId: Int, // Int untuk resource ID drawable/res
    open val role: Boolean
)