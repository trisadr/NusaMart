package com.example.nusamart.feature.buyer.order.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.repository.order.OrderRepository
import com.example.nusamart.data.repository.shipping.ShippingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailVM @Inject constructor(
    private val orderRepository: OrderRepository,
    private val shippingRepository: ShippingRepository,
) : ViewModel() {

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