package com.example.nusamart.entity

data class Buyer(
    val email: String,
    val username: String,
    val password: String,
    val address: String,
    val profilePicResId: Int,
    val role: Boolean = false // Default ke false (0) 
) 
