package com.example.nusamart.feature.buyer.transaction.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.repository.transaction.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentOptionVM @Inject constructor(
    private val paymentRepository: PaymentRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentOptionUiState())
    val uiState = _uiState.asStateFlow()

    init { loadMethods() }

    private fun loadMethods() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val list = paymentRepository.getActivePaymentMethods()

        _uiState.update { it.copy(methods = list, isLoading = false) }
    }
}