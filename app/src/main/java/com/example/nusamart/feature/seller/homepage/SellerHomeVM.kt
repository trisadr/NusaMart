package com.example.nusamart.feature.seller.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.repository.order.OrderRepository
import com.example.nusamart.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerHomeVM @Inject constructor(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository // 1. Inject OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SellerHomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val currentUser = userRepository.getCurrentUser()
        val currentSeller = userRepository.getCurrentSeller()

        _uiState.update {
            it.copy(
                isLoading = false,
                user = currentUser,
                sellerInfo = currentSeller
            )
        }

        // 2. Observasi pesanan secara real-time berdasarkan idStore
        // (Asumsi SellerJson memiliki properti 'idStore')
        currentSeller?.idSeller?.let { storeId ->
            launch { // Buka coroutine baru agar collect tidak memblokir proses lain
                orderRepository.getOrdersFlow().collect { allOrders ->
                    // Filter pesanan khusus untuk toko ini
                    val storeOrders = allOrders.filter { it.idStore == storeId }

                    // Hitung Metrik
                    val newOrdersCount = storeOrders.count { it.orderStatus == "PENDING" }
                    val completedOrders = storeOrders.filter { it.orderStatus == "DELIVERED" }
                    val totalRevenue = completedOrders.sumOf { it.productTotalPrice }

                    // Update state dengan data terbaru
                    _uiState.update { state ->
                        state.copy(
                            newOrdersCount = newOrdersCount,
                            totalRevenue = totalRevenue,
                            // Catatan: Idealnya hitung dari OrderItem, tapi sebagai placeholder
                            // kita hitung jumlah pesanan sukses dulu.
                            productsSold = completedOrders.size
                        )
                    }
                }
            }
        }
    }

    fun logout() = viewModelScope.launch {
        userRepository.logout()
    }
}