package com.example.nusamart.data.model.user

data class UserAddress(
    val idAddress: String,      // PK
    val idUser: String,         // FK (User)
    val label: String,
    val receiver: String,
    val phone: String,
    val completeAddress: String,
    val city: String,
    val province: String,
    val postalCode: String,
    val isDefault: Boolean
)

// JSON-friendly version
data class UserAddressJson(
    val idAddress: String,
    val idUser: String,
    val label: String,
    val receiver: String,
    val phone: String,
    val completeAddress: String,
    val city: String,
    val province: String,
    val postalCode: String,
    val isDefault: Boolean
)