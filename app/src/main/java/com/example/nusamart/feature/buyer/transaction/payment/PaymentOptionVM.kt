package com.example.nusamart.feature.buyer.transaction.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.repository.transaction.PaymentMethodJson
import com.example.nusamart.data.repository.transaction.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentOptionVM @Inject constructor(
    private val transactionRepository: TransactionRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentOptionUiState())
    val uiState = _uiState.asStateFlow()

    init { loadMethods() }
    private fun loadMethods() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        var list = transactionRepository.getActivePaymentMethods()
        // Dummy data jika repository kosong
        if (list.isEmpty()) {
            list = listOf(
                PaymentMethodJson("MET-001", "QRIS NusaMart", "MIDTRANS", true),
                PaymentMethodJson("MET-002", "Transfer Bank BCA", "MANUAL", true),
                PaymentMethodJson("MET-003", "Transfer Bank Mandiri", "MANUAL", true),
                PaymentMethodJson("MET-004", "Transfer Bank BNI", "MANUAL", true),
                PaymentMethodJson("MET-005", "Transfer Bank BRI", "MANUAL", true),
                PaymentMethodJson("MET-006", "Bayar di Tempat (COD)", "COD", true)
            )
        }
        _uiState.update { it.copy(methods = list, isLoading = false) }
    }
}