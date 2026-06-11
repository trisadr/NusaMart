package com.example.nusamart.data.dto

import com.google.gson.annotations.SerializedName

// --- RESPONSE ---

data class StoreWalletDto(
    @SerializedName("idWallet") val idWallet: String,
    @SerializedName("idStore") val idStore: String,
    @SerializedName("activeBalance") val activeBalance: Double,
    @SerializedName("outstandingBalance") val outstandingBalance: Double
)

data class WalletTransactionDto(
    @SerializedName("idTransaction") val idTransaction: String,
    @SerializedName("idWallet") val idWallet: String,
    @SerializedName("mutationType") val mutationType: String, // "IN" | "OUT"
    @SerializedName("nominal") val nominal: Double,
    @SerializedName("description") val description: String? = null,
    @SerializedName("referenceId") val referenceId: String,
    @SerializedName("createAt") val createAt: String? = null
)

data class WithdrawalDto(
    @SerializedName("idWithdrawal") val idWithdrawal: String,
    @SerializedName("idWallet") val idWallet: String,
    @SerializedName("nominal") val nominal: Double,
    @SerializedName("serviceCost") val serviceCost: Double,
    @SerializedName("status") val status: String, // "PENDING" | "PROCESSING" | "DONE" | "FAILED"
    @SerializedName("transferPic") val transferPic: String? = null,
    @SerializedName("createAt") val createAt: String? = null
)

data class WithdrawalActionResponse(
    @SerializedName("message") val message: String,
    @SerializedName("withdrawal") val withdrawal: WithdrawalDto?
)

// --- REQUEST ---

data class WithdrawRequest(
    @SerializedName("nominal") val nominal: Double,
    @SerializedName("serviceCost") val serviceCost: Double
)