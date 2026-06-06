package com.example.nusamart.feature.seller.order.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.model.order.Order
import com.example.nusamart.data.model.shipping.Shipping
import com.example.nusamart.data.repository.order.OrderRepository
import com.example.nusamart.data.repository.shipping.ShippingRepository
import com.example.nusamart.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomingOrderDetailVM @Inject constructor(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val shippingRepository: ShippingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomingOrderDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadOrderDetail(orderId: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val orderData = orderRepository.getOrderById(orderId)
        if (orderData != null) {
            val itemsData = orderRepository.getOrderItems(orderId)
            val buyerData = userRepository.getUserById(orderData.idUser)

            _uiState.update {
                it.copy(isLoading = false, order = orderData, items = itemsData, buyer = buyerData)
            }
        } else {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Pesanan tidak ditemukan") }
        }
    }

    // 1. PROSES PESANAN (PENDING -> PROCESSED)
    fun processOrder(orderId: String, courierId: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        // PERBAIKAN: Gunakan .name agar pasti tersimpan sebagai String "PROCESSED"
        orderRepository.updateOrderStatus(orderId, Order.OrderStatus.PROCESSED)
        shippingRepository.createShipping(orderId, courierId)

        loadOrderDetail(orderId)
    }

    // 2. BATALKAN PESANAN (PENDING -> CANCELLED)
    fun cancelOrder(orderId: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        orderRepository.updateOrderStatus(orderId, Order.OrderStatus.CANCELLED)
        loadOrderDetail(orderId)
    }

    // 3. KIRIM PESANAN (PROCESSED -> SHIPPED)
    fun shipOrder(orderId: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        orderRepository.updateOrderStatus(orderId, Order.OrderStatus.SHIPPED)

        val shipping = shippingRepository.getShippingByOrderId(orderId)
        if (shipping != null) {
            val dummyResi = "NUSA-${System.currentTimeMillis()}"
            shippingRepository.updateShippingStatus(
                shippingId = shipping.idShipping,
                newStatus = Shipping.ShippingStatus.PICKED_UP,
                resiNumber = dummyResi
            )
            shippingRepository.addTrackingUpdate(
                shippingId = shipping.idShipping,
                location = "Toko Penjual",
                description = "Paket telah diserahkan ke pihak kurir dengan resi $dummyResi"
            )
        }

        loadOrderDetail(orderId)
    }
}