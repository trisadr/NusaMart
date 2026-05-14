package com.example.nusamart.data.repository.transaction

import android.content.Context
import com.example.nusamart.data.model.transaction.Payment
import com.example.nusamart.data.model.transaction.PaymentMethod
import com.example.nusamart.data.model.transaction.WalletTransaction
import com.example.nusamart.data.model.transaction.Withdrawal
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

// ─── JSON-Friendly Models ─────────────────────────────────────────────────────

data class PaymentJson(
    val idPayment: String,
    val idOrder: String,
    val idMethod: String,
    val transactionIdGateway: String? = null,
    val snapToken: String? = null,
    val paymentStatus: String,
    val paymentTime: String? = null
)

data class PaymentMethodJson(
    val idMethod: String,
    val methodName: String,
    val provider: String,
    val isActive: Boolean
)

data class StoreWalletJson(
    val idWallet: String,
    val idStore: String,
    val activeBalance: Double,
    val outstandingBalance: Double
)

data class WalletTransactionJson(
    val idTransaction: String,
    val idWallet: String,
    val mutationType: String,
    val nominal: Double,
    val description: String? = null,
    val referenceId: String
)

data class WithdrawalJson(
    val idWithdrawal: String,
    val idWallet: String,
    val nominal: Double,
    val serviceCost: Double,
    val status: String,
    val transferPic: Int? = null
)

// ─── Hasil Operasi ───────────────────────────────────────────────────────────

sealed class TransactionResult {
    data class Success(val transactionId: String) : TransactionResult()
    data class Error(val message: String) : TransactionResult()
}

// ─── Repository ──────────────────────────────────────────────────────────────

class TransactionRepository(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private val paymentFile = "payment.json"
    private val paymentMethodFile = "payment_method.json"
    private val walletFile = "store_wallet.json"
    private val walletTransactionFile = "wallet_transaction.json"
    private val withdrawalFile = "withdrawal.json"

    // ─── Helper Baca/Tulis JSON ───────────────────────────────────────────────

    private inline fun <reified T> readJson(fileName: String): MutableList<T> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            // Coba ambil dari assets (sangat berguna untuk master data seperti PaymentMethod)
            try {
                context.assets.open(fileName).use { inputStream ->
                    val json = inputStream.bufferedReader().readText()
                    file.writeText(json)
                }
            } catch (e: Exception) {
                return mutableListOf()
            }
        }
        val json = file.readText()
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    private fun <T> writeJson(fileName: String, data: List<T>) {
        val file = File(context.filesDir, fileName)
        file.writeText(gson.toJson(data))
    }

    // ==========================================
    // MANAJEMEN PEMBAYARAN (PAYMENT)
    // ==========================================

    suspend fun getActivePaymentMethods(): List<PaymentMethodJson> = withContext(Dispatchers.IO) {
        val methods = readJson<PaymentMethodJson>(paymentMethodFile)
        return@withContext methods.filter { it.isActive }
    }

    suspend fun getPaymentByOrderId(orderId: String): PaymentJson? = withContext(Dispatchers.IO) {
        val payments = readJson<PaymentJson>(paymentFile)
        return@withContext payments.find { it.idOrder == orderId }
    }

    suspend fun createPayment(
        orderId: String,
        methodId: String,
        transactionIdGateway: String? = null,
        snapToken: String? = null
    ): TransactionResult = withContext(Dispatchers.IO) {
        delay(500)
        val payments = readJson<PaymentJson>(paymentFile)

        if (payments.any { it.idOrder == orderId }) {
            return@withContext TransactionResult.Error("Pembayaran untuk pesanan ini sudah ada.")
        }

        val maxPayNum = payments.maxOfOrNull { it.idPayment.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
        val newPaymentId = String.format("PAY-%06d", maxPayNum + 1)

        val newPayment = PaymentJson(
            idPayment = newPaymentId,
            idOrder = orderId,
            idMethod = methodId,
            transactionIdGateway = transactionIdGateway,
            snapToken = snapToken,
            paymentStatus = Payment.PaymentStatus.PENDING.name,
            paymentTime = null
        )

        payments.add(newPayment)
        writeJson(paymentFile, payments)

        return@withContext TransactionResult.Success(newPaymentId)
    }

    suspend fun updatePaymentStatus(
        paymentId: String,
        newStatus: Payment.PaymentStatus
    ): Boolean = withContext(Dispatchers.IO) {
        val payments = readJson<PaymentJson>(paymentFile)
        val index = payments.indexOfFirst { it.idPayment == paymentId }

        if (index != -1) {
            val isApproved = newStatus == Payment.PaymentStatus.APPROVED
            payments[index] = payments[index].copy(
                paymentStatus = newStatus.name,
                paymentTime = if (isApproved) LocalDateTime.now().toString() else payments[index].paymentTime
            )
            writeJson(paymentFile, payments)
            return@withContext true
        }
        return@withContext false
    }

    // ==========================================
    // MANAJEMEN DOMPET TOKO (STORE WALLET)
    // ==========================================

    suspend fun getWalletByStoreId(storeId: String): StoreWalletJson? = withContext(Dispatchers.IO) {
        val wallets = readJson<StoreWalletJson>(walletFile)
        return@withContext wallets.find { it.idStore == storeId }
    }

    suspend fun initializeWalletForStore(storeId: String): String = withContext(Dispatchers.IO) {
        val wallets = readJson<StoreWalletJson>(walletFile)

        // Kembalikan ID jika sudah ada
        val existingWallet = wallets.find { it.idStore == storeId }
        if (existingWallet != null) return@withContext existingWallet.idWallet

        val maxWalNum = wallets.maxOfOrNull { it.idWallet.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
        val newWalletId = String.format("WAL-%06d", maxWalNum + 1)

        val newWallet = StoreWalletJson(
            idWallet = newWalletId,
            idStore = storeId,
            activeBalance = 0.0,
            outstandingBalance = 0.0
        )
        wallets.add(newWallet)
        writeJson(walletFile, wallets)

        return@withContext newWalletId
    }

    // Digunakan untuk menambah outstanding balance (saat pesanan dibayar)
    // atau memindahkannya ke active balance (saat pesanan selesai)
    suspend fun updateWalletBalances(
        walletId: String,
        addActive: Double = 0.0,
        addOutstanding: Double = 0.0
    ): Boolean = withContext(Dispatchers.IO) {
        val wallets = readJson<StoreWalletJson>(walletFile)
        val index = wallets.indexOfFirst { it.idWallet == walletId }

        if (index != -1) {
            val wallet = wallets[index]
            wallets[index] = wallet.copy(
                activeBalance = wallet.activeBalance + addActive,
                outstandingBalance = wallet.outstandingBalance + addOutstanding
            )
            writeJson(walletFile, wallets)
            return@withContext true
        }
        return@withContext false
    }

    // ==========================================
    // MUTASI TRANSAKSI DOMPET (WALLET TRANSACTION)
    // ==========================================

    suspend fun getWalletTransactions(walletId: String): List<WalletTransactionJson> = withContext(Dispatchers.IO) {
        val transactions = readJson<WalletTransactionJson>(walletTransactionFile)
        return@withContext transactions.filter { it.idWallet == walletId }
    }

    suspend fun addWalletTransaction(
        walletId: String,
        mutationType: WalletTransaction.MutationType,
        nominal: Double,
        description: String?,
        referenceId: String
    ): TransactionResult = withContext(Dispatchers.IO) {
        val transactions = readJson<WalletTransactionJson>(walletTransactionFile)

        val maxWtrNum = transactions.maxOfOrNull { it.idTransaction.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
        val newTransactionId = String.format("WTR-%06d", maxWtrNum + 1)

        val newTransaction = WalletTransactionJson(
            idTransaction = newTransactionId,
            idWallet = walletId,
            mutationType = mutationType.name,
            nominal = nominal,
            description = description,
            referenceId = referenceId
        )

        transactions.add(newTransaction)
        writeJson(walletTransactionFile, transactions)

        return@withContext TransactionResult.Success(newTransactionId)
    }

    // ==========================================
    // PENARIKAN DANA (WITHDRAWAL)
    // ==========================================

    suspend fun getWithdrawalsByWalletId(walletId: String): List<WithdrawalJson> = withContext(Dispatchers.IO) {
        val withdrawals = readJson<WithdrawalJson>(withdrawalFile)
        return@withContext withdrawals.filter { it.idWallet == walletId }
    }

    suspend fun createWithdrawal(
        walletId: String,
        nominal: Double,
        serviceCost: Double
    ): TransactionResult = withContext(Dispatchers.IO) {
        delay(500)
        val withdrawals = readJson<WithdrawalJson>(withdrawalFile)

        // Validasi saldo
        val wallets = readJson<StoreWalletJson>(walletFile)
        val wallet = wallets.find { it.idWallet == walletId }
        val totalDeduction = nominal + serviceCost

        if (wallet == null || wallet.activeBalance < totalDeduction) {
            return@withContext TransactionResult.Error("Saldo aktif tidak mencukupi untuk melakukan penarikan.")
        }

        // Potong saldo aktif secara langsung saat request
        updateWalletBalances(walletId, addActive = -totalDeduction)

        val maxWdlNum = withdrawals.maxOfOrNull { it.idWithdrawal.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
        val newWithdrawalId = String.format("WDL-%06d", maxWdlNum + 1)

        val newWithdrawal = WithdrawalJson(
            idWithdrawal = newWithdrawalId,
            idWallet = walletId,
            nominal = nominal,
            serviceCost = serviceCost,
            status = Withdrawal.WithdrawalStatus.PENDING.name,
            transferPic = null
        )

        withdrawals.add(newWithdrawal)
        writeJson(withdrawalFile, withdrawals)

        // Catat mutasi OUT
        addWalletTransaction(
            walletId = walletId,
            mutationType = WalletTransaction.MutationType.OUT,
            nominal = totalDeduction,
            description = "Penarikan Dana",
            referenceId = newWithdrawalId
        )

        return@withContext TransactionResult.Success(newWithdrawalId)
    }

    suspend fun updateWithdrawalStatus(
        withdrawalId: String,
        newStatus: Withdrawal.WithdrawalStatus,
        transferPicResId: Int? = null
    ): Boolean = withContext(Dispatchers.IO) {
        val withdrawals = readJson<WithdrawalJson>(withdrawalFile)
        val index = withdrawals.indexOfFirst { it.idWithdrawal == withdrawalId }

        if (index != -1) {
            withdrawals[index] = withdrawals[index].copy(
                status = newStatus.name,
                transferPic = transferPicResId ?: withdrawals[index].transferPic
            )
            writeJson(withdrawalFile, withdrawals)

            // Jika GAGAL, kembalikan dana ke active balance
            if (newStatus == Withdrawal.WithdrawalStatus.FAILED) {
                val failedWdl = withdrawals[index]
                val totalRefund = failedWdl.nominal + failedWdl.serviceCost
                updateWalletBalances(failedWdl.idWallet, addActive = totalRefund)

                // Catat mutasi IN (Pengembalian Dana)
                addWalletTransaction(
                    walletId = failedWdl.idWallet,
                    mutationType = WalletTransaction.MutationType.IN,
                    nominal = totalRefund,
                    description = "Pengembalian Dana Penarikan Gagal",
                    referenceId = withdrawalId
                )
            }
            return@withContext true
        }
        return@withContext false
    }
}