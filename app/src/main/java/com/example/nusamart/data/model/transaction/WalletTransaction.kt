package com.example.nusamart.data.model.transaction

data class WalletTransaction(
    val idTransaction: String,      // PK
    val idWallet: String,           // FK (StoreWallet)
    val mutationType: MutationType,
    val nominal: Double,
    val description: String? = null,
    val referenceId: String         // FK (Order.idOrder / Withdrawal.idWithdrawal)
) {
    enum class MutationType {
        IN,
        OUT
    }
}
