package com.example.nusamart.data.interfaceapi

import com.example.nusamart.data.dto.StoreWalletDto
import com.example.nusamart.data.dto.WalletTransactionDto
import com.example.nusamart.data.dto.WithdrawRequest
import com.example.nusamart.data.dto.WithdrawalActionResponse
import com.example.nusamart.data.dto.WithdrawalDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WalletApi {

    // GET /api/seller/wallet
    @GET("seller/wallet")
    suspend fun getWallet(): StoreWalletDto

    // GET /api/seller/wallet/transactions
    @GET("seller/wallet/transactions")
    suspend fun getWalletTransactions(): List<WalletTransactionDto>

    // GET /api/seller/wallet/withdrawals
    @GET("seller/wallet/withdrawals")
    suspend fun getWithdrawals(): List<WithdrawalDto>

    // POST /api/seller/wallet/withdraw
    @POST("seller/wallet/withdraw")
    suspend fun withdraw(@Body request: WithdrawRequest): WithdrawalActionResponse
}