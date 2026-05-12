package com.example.nusamart.data.model.user

data class Admin(
    val idAdmin: String,        // PK + FK (User)
    val division: String,       // Nanti ganti pakai enum
    val accessLevel: String     // Nanti ganti pakai enum
)

// Apa admin ga perlu ada class di mobile nya? - rifqia
