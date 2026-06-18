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

        // 1. Ambil profil untuk mendapatkan data user
        val profile = userRepository.getCurrentProfile()

        // GANTI BARIS YANG MERAH MENJADI INI:
        // Cukup pastikan objek seller tidak null (berarti dia benar-benar penjual)
        val isSeller = profile?.seller != null

        // Ubah pengecekannya menggunakan isSeller
        if (isSeller) {
            // 2. Ambil semua pesanan yang masuk ke toko ini dari API
            val allSellerOrders = orderRepository.getSellerOrders()

            // ====================================================================================
            // LOGIKA METRIK DAHBOARD (Berdasarkan permintaan Trisa)
            // ====================================================================================

            // A. Pesanan Baru (Status Menunggu - PENDING)
            // Menghitung jumlah order yang statusnya masih PENDING
            val newOrdersCount = allSellerOrders.count { it.orderStatus == "PENDING" }

            // B. Jumlah Barang Terjual
            // Dihitung dari order yang sedang dikirim (SHIPPED) DAN yang sudah selesai (DELIVERED)
            val productsSold = allSellerOrders
                .filter { it.orderStatus == "SHIPPED" || it.orderStatus == "DELIVERED" }
                .flatMap { it.orderItems }
                .sumOf { it.quantity }

            // C. Total Pendapatan (Status Dikonfirmasi Selesai - DELIVERED)
            // Menjumlahkan productTotalPrice (total harga barang, tanpa ongkir)
            // dari order yang statusnya sudah DELIVERED.
            val totalRevenue = allSellerOrders
                .filter { it.orderStatus == "DELIVERED" } // Filter order selesai
                .sumOf { it.productTotalPrice } // Jumlahkan harga produk

            // ====================================================================================

            // 3. Update UI State dengan data dinamis
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
            // Jika bukan seller atau terjadi kesalahan
            _uiState.update { it.copy(isLoading = false, user = profile) }
        }
    }

    fun logout() = viewModelScope.launch {
        userRepository.logout()
    }
}