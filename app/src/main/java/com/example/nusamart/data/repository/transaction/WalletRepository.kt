package com.example.nusamart.data.repository.transaction

import com.example.nusamart.data.dto.StoreWalletDto
import com.example.nusamart.data.dto.WalletTransactionDto
import com.example.nusamart.data.dto.WithdrawRequest
import com.example.nusamart.data.dto.WithdrawalDto
import com.example.nusamart.data.interfaceapi.WalletApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class WalletResult {
    data class Success(val id: String) : WalletResult()
    data class Error(val message: String) : WalletResult()
}

@Singleton
class WalletRepository @Inject constructor(
    private val apiService: WalletApi
) {

    suspend fun getWallet(): StoreWalletDto? = withContext(Dispatchers.IO) {
        try {
            apiService.getWallet()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getWalletTransactions(): List<WalletTransactionDto> = withContext(Dispatchers.IO) {
        try {
            apiService.getWalletTransactions()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getWithdrawals(): List<WithdrawalDto> = withContext(Dispatchers.IO) {
        try {
            apiService.getWithdrawals()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun withdraw(
        nominal: Double,
        serviceCost: Double
    ): WalletResult = withContext(Dispatchers.IO) {
        try {
            val request = WithdrawRequest(nominal = nominal, serviceCost = serviceCost)
            val response = apiService.withdraw(request)
            val withdrawalId = response.withdrawal?.idWithdrawal
                ?: return@withContext WalletResult.Error("Withdrawal ID tidak ditemukan")
            WalletResult.Success(withdrawalId)
        } catch (e: Exception) {
            WalletResult.Error(e.message ?: "Gagal melakukan penarikan dana")
        }
    }
}