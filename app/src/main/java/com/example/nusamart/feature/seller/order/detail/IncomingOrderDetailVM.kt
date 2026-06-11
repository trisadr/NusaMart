package com.example.nusamart.feature.seller.order.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.model.order.Order
import com.example.nusamart.data.model.shipping.Shipping
import com.example.nusamart.data.repository.notif.NotificationRepository
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
    private val shippingRepository: ShippingRepository,
    private val notificationRepository: NotificationRepository
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

        orderRepository.updateOrderStatus(orderId, Order.OrderStatus.PROCESSED.name)
        shippingRepository.createShipping(orderId, courierId)

        // AMBIL DATA & KIRIM NOTIFIKASI KE BUYER
        val order = orderRepository.getOrderById(orderId)
        if (order != null) {
            val items = orderRepository.getOrderItems(orderId)
            val productNames = items.joinToString(", ") { it.nameSnapshot }
            notificationRepository.addOrderStatusNotification(order.idUser, orderId, productNames, "PROCESSED")
        }

        loadOrderDetail(orderId)
    }

    // 2. BATALKAN PESANAN (PENDING -> CANCELLED)
    fun cancelOrder(orderId: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        // Update status order
        orderRepository.updateOrderStatus(orderId, Order.OrderStatus.CANCELLED.name)

        // AMBIL DATA ORDER UNTUK MENDAPATKAN ID BUYER
        val order = orderRepository.getOrderById(orderId)
        if (order != null) {

            // 1. AMBIL NAMA-NAMA PRODUK DARI PESANAN INI
            val items = orderRepository.getOrderItems(orderId)
            // Gabungkan semua nama produk menjadi satu teks (misal: "Kopi Lokal, Gula Aren")
            val productNames = items.joinToString(", ") { it.nameSnapshot }

            // 2. KIRIM NOTIFIKASI PEMBATALAN BESERTA NAMA PRODUK
            notificationRepository.addOrderCancelledNotification(
                userId = order.idUser,
                orderId = orderId,
                productNames = productNames // <-- KIRIMKAN KE SINI
            )
        }

        loadOrderDetail(orderId)
    }

    // 3. KIRIM PESANAN (PROCESSED -> SHIPPED)
    fun shipOrder(orderId: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        orderRepository.updateOrderStatus(orderId, Order.OrderStatus.SHIPPED.name)

        val shipping = shippingRepository.getShippingByOrderId(orderId)
        if (shipping != null) {
            val dummyResi = "NUSA-${System.currentTimeMillis()}"
            shippingRepository.updateShippingStatus(
                shippingId = shipping.idShipping,
                newStatus = Shipping.ShippingStatus.PICKED_UP.name,
                resiNumber = dummyResi
            )
            shippingRepository.addTrackingUpdate(
                shippingId = shipping.idShipping,
                location = "Toko Penjual",
                description = "Paket telah diserahkan ke pihak kurir dengan resi $dummyResi"
            )
        }

        // AMBIL DATA & KIRIM NOTIFIKASI KE BUYER
        val order = orderRepository.getOrderById(orderId)
        if (order != null) {
            val items = orderRepository.getOrderItems(orderId)
            val productNames = items.joinToString(", ") { it.nameSnapshot }
            notificationRepository.addOrderStatusNotification(order.idUser, orderId, productNames, "SHIPPED")
        }

        loadOrderDetail(orderId)
    }
}