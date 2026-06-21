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

        val profile = userRepository.getCurrentProfile()

        val isSeller = profile?.seller != null

        if (isSeller) {
            val allSellerOrders = orderRepository.getSellerOrders()
            val newOrdersCount = allSellerOrders.count { it.orderStatus == "PENDING" }

            val productsSold = allSellerOrders
                .filter { it.orderStatus == "SHIPPED" || it.orderStatus == "DELIVERED" }
                .flatMap { it.orderItems }
                .sumOf { it.quantity }
            val totalRevenue = allSellerOrders
                .filter { it.orderStatus == "DELIVERED" }
                .sumOf { it.productTotalPrice }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    user = profile,
                    sellerInfo = profile?.seller,
                    newOrdersCount = newOrdersCount,
                    totalRevenue = totalRevenue.toLong(),
                    productsSoldCount = productsSold
                )
            }
        } else {
            _uiState.update { it.copy(isLoading = false, user = profile) }
        }
    }

    fun logout() = viewModelScope.launch {
        userRepository.logout()
    }
}