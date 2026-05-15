package com.example.nusamart.data.model.store

data class Store(
    val idStore: String,        // PK
    val idSeller: String,       // FK (Seller)
    val name: String,
    val description: String,
    val logoURL: Int? = null,
    val location: String,
    val urlLocation: String?,
    val createAt: java.time.LocalDateTime,
    val updateAt: java.time.LocalDateTime,
    val storeRating: Double? = null,
    val isActive: Boolean
)