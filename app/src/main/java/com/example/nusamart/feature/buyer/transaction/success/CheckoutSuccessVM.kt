package com.example.nusamart.feature.buyer.transaction.success

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.core.MyApplication
import com.example.nusamart.data.repository.transaction.TransactionRepository
import com.example.nusamart.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- VIEWMODEL ---
class CheckoutSuccessVM(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                CheckoutSuccessVM(app.transactionRepository, app.userRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(SuccessUiState())
    val uiState = _uiState.asStateFlow()

    fun loadData(paymentId: String, orderId: String) = viewModelScope.launch {
        // 1. Tarik data payment dari orderId
        val payment = transactionRepository.getPaymentByOrderId(orderId)
        val methods = transactionRepository.getActivePaymentMethods()

        // 2. Cari metode pembayaran yang digunakan
        val selectedMethod = methods.find { it.idMethod == payment?.idMethod }
        val selectedProvider = selectedMethod?.provider ?: "COD"
        val methodName = selectedMethod?.methodName ?: "Bayar di Tempat"

        // 3. Tarik data user untuk dummy Virtual Account
        val user = userRepository.getCurrentUser()
        val phone = user?.phone ?: "081234567890"

        var code = ""
        when (selectedProvider) {
            "MIDTRANS" -> code = "QRIS" // Gambar QR akan di-handle di UI
            "MANUAL" -> {
                val prefix = when {
                    methodName.contains("BCA", ignoreCase = true) -> "014"
                    methodName.contains("Mandiri", ignoreCase = true) -> "008"
                    methodName.contains("BNI", ignoreCase = true) -> "009"
                    methodName.contains("BRI", ignoreCase = true) -> "002"
                    else -> "000"
                }
                code = prefix + phone
            }
            "COD" -> code = ""
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                provider = selectedProvider,
                paymentCode = code,
                bankName = methodName
            )
        }
    }
}