package com.example.nusamart.data.model.user

data class Seller(
    val idSeller: String,       // PK + FK (User)
    val nik: String,
    val ktpPhoto: Int,
    val bankName: String,
    val accountNumber: String
)

// JSON-friendly version
data class SellerJson(
    val idSeller: String,
    val nik: String,
    val ktpPhoto: Int,
    val bankName: String,
    val accountNumber: String
)