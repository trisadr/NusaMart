package com.example.nusamart.feature.seller.order.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.repository.order.OrderRepository
import com.example.nusamart.data.repository.store.StoreRepository
import com.example.nusamart.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomingOrderListVM @Inject constructor(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomingOrderListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadIncomingOrders()
    }

    fun loadIncomingOrders() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        // 1. LANGSUNG PANGGIL TOKO MILIK SELLER DARI API
        // Tidak perlu lagi mem-passing sellerId karena API sudah membacanya dari Token
        val myStore = storeRepository.getMyStore()

        // 2. JIKA TOKO DITEMUKAN, AMBIL DAFTAR PESANANNYA
        if (myStore != null) {
            val incomingOrders = orderRepository.getSellerOrders()

            val uiModels = incomingOrders.map { order ->
                val items = orderRepository.getOrderItems(order.idOrder)
                val firstItemName = items.firstOrNull()?.nameSnapshot ?: "Memuat produk..."
                val additionalCount = if (items.size > 1) items.size - 1 else 0

                val buyer = userRepository.getUserById(order.idUser)
                val buyerNameDisplay = buyer?.username ?: order.idUser

                IncomingOrderListUiModel(
                    order = order,
                    buyerName = buyerNameDisplay,
                    firstItemName = firstItemName,
                    additionalItemCount = additionalCount
                )
            }

            val sortedModels = uiModels.sortedByDescending { it.order.orderDate }
            _uiState.update { it.copy(orders = sortedModels, isLoading = false) }
        } else {
            // Jika API merespons kosong (seller belum buat toko)
            _uiState.update { it.copy(isLoading = false, orders = emptyList()) }
        }
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }
}