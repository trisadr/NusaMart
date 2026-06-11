package com.example.nusamart.feature.buyer.cart

data class CartItemUiModel(
    val idCartItem: String,
    val idItem: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val isChecked: Int,
    val imageUrl: String? = null, // Tambahkan imageUrl jika diperlukan: String
)

data class StoreCartGroup(
    val storeId: String,
    val storeName: String,
    val items: List<CartItemUiModel>
)

data class CartUiState(
    val isLoading: Boolean = true,
    val storeGroups: List<StoreCartGroup> = emptyList(),
    val totalPrice: Double = 0.0,
    val checkedCount: Int = 0,
    val isAllChecked: Int = 0
)