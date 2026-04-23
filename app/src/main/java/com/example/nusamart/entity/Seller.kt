package com.example.nusamart.entity

data class BankAccount(
    val bankName: String,
    val accountNumber: String,
    val accountHolder: String
)

data class Seller(
    override val email: String,
    override val username: String,
    override val password: String,
    override val address: String,
    override val profilePicResId: Int,
    override val role: Boolean = true, // Default ke true (1) untuk Seller

    // Properti khusus Seller
    val account: BankAccount,
    val idStore: String,
    val storeName: String,
    val description: String,
    val verifiedStatus: Boolean
) : User(email, username, password, address, profilePicResId, role)
