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
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SellerHomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        // 1. Cukup panggil 1x karena data user dan seller sudah digabung dari API
        val profile = userRepository.getCurrentProfile()

        _uiState.update {
            it.copy(
                isLoading = false,
                user = profile, // Mengisi data user utama
                sellerInfo = profile?.seller // Mengambil objek seller dari dalam profile
            )
        }

//        // 2. Gunakan idUser dari profile sebagai ID Toko (storeId)
//        profile?.idUser?.let { storeId ->
//            launch {
//                orderRepository.getOrdersFlow().collect { allOrders ->
//                    val storeOrders = allOrders.filter { it.idStore == storeId }
//
//                    val newOrdersCount = storeOrders.count { it.orderStatus == "PENDING" }
//                    val completedOrders = storeOrders.filter { it.orderStatus == "DELIVERED" }
//                    val totalRevenue = completedOrders.sumOf { it.productTotalPrice }
//
//                    _uiState.update { state ->
//                        state.copy(
//                            newOrdersCount = newOrdersCount,
//                            totalRevenue = totalRevenue,
//                            productsSold = completedOrders.size
//                        )
//                    }
//                }
//            }
//        }
    }

    fun logout() = viewModelScope.launch {
        userRepository.logout()
    }
}