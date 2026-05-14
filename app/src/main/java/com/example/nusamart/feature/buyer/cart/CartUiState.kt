package com.example.nusamart.feature.buyer.cart

data class CartItemUiModel(
    val idCartItem: String,
    val idItem: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val isChecked: Boolean,
    val imageResId: Int
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
    val isAllChecked: Boolean = false
)