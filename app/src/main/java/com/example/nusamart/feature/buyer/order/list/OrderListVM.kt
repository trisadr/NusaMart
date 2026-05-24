package com.example.nusamart.feature.buyer.order.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.core.MyApplication
import com.example.nusamart.data.repository.order.OrderJson
import com.example.nusamart.data.repository.order.OrderRepository
import com.example.nusamart.data.repository.store.StoreRepository
import com.example.nusamart.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderListVM(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository // Tambahkan Store Repository
) : ViewModel() {

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                OrderListVM(
                    app.orderRepository,
                    app.userRepository,
                    app.storeRepository // Pass dependency-nya
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(OrderListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    // Ubah jadi public (hapus 'private') agar bisa dipanggil LaunchedEffect jika butuh refresh
    fun loadOrders() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        val userId = userRepository.getActiveUserId()

        if (userId != null) {
            val userOrders = orderRepository.getOrdersByUser(userId)
            val allStores = storeRepository.getAllStores() // Ambil daftar toko

            // Gabungkan data Order, Store, dan OrderItem
            val uiModels = userOrders.map { order ->
                val store = allStores.find { it.idStore == order.idStore }
                val items = orderRepository.getOrderItems(order.idOrder)

                val storeName = store?.name ?: "Toko Tidak Diketahui"
                val firstItemName = items.firstOrNull()?.nameSnapshot ?: "Memuat produk..."

                // Menghitung apakah ada barang lain di pesanan yang sama
                val additionalCount = if (items.size > 1) items.size - 1 else 0

                OrderListUiModel(
                    order = order,
                    storeName = storeName,
                    firstItemName = firstItemName,
                    additionalItemCount = additionalCount
                )
            }

            _uiState.update { it.copy(orders = uiModels, isLoading = false) }
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }
}