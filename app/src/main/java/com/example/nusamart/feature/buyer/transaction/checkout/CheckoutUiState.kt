package com.example.nusamart.feature.buyer.transaction.checkout

import com.example.nusamart.data.model.user.UserAddressJson
import com.example.nusamart.data.repository.order.OrderItemInput

// --- STATE ---
data class CheckoutUiState(
    val isLoading: Boolean = true,
    val items: List<OrderItemInput> = emptyList(),
    val address: UserAddressJson? = null,
    val courierName: String = "Pilih Kurir Pengiriman",
    val paymentName: String = "Pilih Metode Pembayaran",
    val subtotal: Double = 0.0,
    val shippingCost: Double = 15000.0,
    val serviceFee: Double = 2500.0,
    val showAddressDialog: Boolean = false,
    val orderStoreId: String = "" // Asumsi semua item dari toko yang sama
)