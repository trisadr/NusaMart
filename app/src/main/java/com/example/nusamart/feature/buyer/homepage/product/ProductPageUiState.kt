package com.example.nusamart.feature.buyer.homepage.product

data class ProductItemUiModel(
    val idItem: String,
    val price: Double,
    val stock: Int,
    val variationName: String
)

data class ProductPageUiState(
    val isLoading: Boolean = true,
    val productId: String = "",
    val productName: String = "",
    val productDescription: String = "",
    val images: List<String> = emptyList(),
    val minPrice: Double = 0.0,
    val maxPrice: Double = 0.0,
    val totalStock: Int = 0,
    val soldCount: Int = 0, // <-- Variabel baru
    val storeId: String = "",
    val storeName: String = "",
    val storeLocation: String = "",
    val storeUrlLocation: String? = null,
    val mapUrl: String = "",
    val items: List<ProductItemUiModel> = emptyList(),
    val selectedItemId: String? = null,
    val quantity: Int = 1,
    val sheetMode: SheetMode = SheetMode.NONE,
    val isStoreVerified: Boolean = false
)

enum class SheetMode { NONE, CART, BUY }