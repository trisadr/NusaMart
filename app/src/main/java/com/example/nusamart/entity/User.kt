package com.example.nusamart.entity

abstract class User(
    open val email: String,
    open val username: String,
    open val password: String, 
    open val address: String, 
    open val profilePicResId: Int, 
    open val role: Boolean
)
