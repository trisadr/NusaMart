package com.example.nusamart.data.model.transaction

data class StoreWallet(
    val idWallet: String,           // PK
    val idStore: String,            // FK (Store)
    val activeBalance: Double,
    val outstandingBalance: Double
)
