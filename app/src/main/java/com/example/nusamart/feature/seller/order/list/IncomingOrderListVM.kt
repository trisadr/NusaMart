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
    private val storeRepository: StoreRepository // INJEKSI STORE REPOSITORY
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomingOrderListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadIncomingOrders()
    }

    fun loadIncomingOrders() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val sellerId = userRepository.getActiveUserId()

        if (sellerId != null) {

            // 1. CARI TOKO MILIK SELLER INI TERLEBIH DAHULU
            val myStore = storeRepository.getStoreBySellerId(sellerId)

            // 2. JIKA DIA PUNYA TOKO, AMBIL PESANAN BERDASARKAN ID TOKO TERSEBUT
            if (myStore != null) {
                val incomingOrders = orderRepository.getOrdersByStore(myStore.idStore)

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
                // Jika seller belum buat toko
                _uiState.update { it.copy(isLoading = false, orders = emptyList()) }
            }
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }
}