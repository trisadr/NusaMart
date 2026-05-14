package com.example.nusamart.feature.buyer.order.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.core.MyApplication
import com.example.nusamart.data.repository.order.OrderRepository
import com.example.nusamart.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderListVM(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                OrderListVM(app.orderRepository, app.userRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(OrderListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        val userId = userRepository.getActiveUserId()

        if (userId != null) {
            val userOrders = orderRepository.getOrdersByUser(userId)
            _uiState.update { it.copy(orders = userOrders, isLoading = false) }
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }
}