package com.example.nusamart.data.model.transaction

data class Withdrawal(
    val idWithdrawal: String,       // PK
    val idWallet: String,           // FK (StoreWallet)
    val nominal: Double,
    val serviceCost: Double,
    val status: WithdrawalStatus,
    val transferPic: Int? = null
) {
    enum class WithdrawalStatus {
        PENDING,
        PROCESSING,
        DONE,
        FAILED
    }
}
