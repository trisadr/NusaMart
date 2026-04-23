package com.example.nusamart.entity

data class Buyer(
    override val email: String,
    override val username: String,
    override val password: String,
    override val address: String,
    override val profilePicResId: Int,
    override val role: Boolean = false // Default ke false (0) untuk Buyer
) : User(email, username, password, address, profilePicResId, role)