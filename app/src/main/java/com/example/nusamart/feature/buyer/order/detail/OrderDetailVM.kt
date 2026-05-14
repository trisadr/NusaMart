package com.example.nusamart.feature.buyer.order.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.core.MyApplication
import com.example.nusamart.data.repository.order.OrderRepository
import com.example.nusamart.data.repository.shipping.ShippingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderDetailVM(
    private val orderRepository: OrderRepository,
    private val shippingRepository: ShippingRepository
) : ViewModel() {

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                OrderDetailVM(app.orderRepository, app.shippingRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadOrderDetail(orderId: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val order = orderRepository.getOrderById(orderId)
        if (order == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Pesanan tidak ditemukan.") }
            return@launch
        }

        val items = orderRepository.getOrderItems(orderId)
        val isReviewed = orderRepository.isOrderReviewed(orderId)

        // Ambil data resi dari Shipping Repository
        val shipping = shippingRepository.getShippingByOrderId(orderId)
        val resi = shipping?.resi ?: "Belum dikirim"

        _uiState.update {
            it.copy(
                isLoading = false,
                order = order,
                orderItems = items,
                isReviewed = isReviewed,
                resiNumber = resi
            )
        }
    }
}