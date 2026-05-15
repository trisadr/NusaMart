package com.example.nusamart.data.model.user

data class Seller(
    val idSeller: String,       // PK + FK (User)
    val nik: String,
    val ktpPhoto: Int,
    val bankName: String,
    val accountNumber: String
)

// JSON-friendly version -- mungkin bisa pindah di repository aja entar
data class SellerJson(
    val idSeller: String,
    val nik: String,
    val ktpPhoto: Int,
    val bankName: String,
    val accountNumber: String
)