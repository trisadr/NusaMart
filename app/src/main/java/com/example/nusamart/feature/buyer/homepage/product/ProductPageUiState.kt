package com.example.nusamart.feature.buyer.homepage.product

data class ProductItemUiModel(
    val idItem: String,
    val price: Double,
    val stock: Int,
    val variationName: String // Gabungan variasi, misal: "Merah - XL"
)

data class ProductPageUiState(
    val isLoading: Boolean = true,
    val productId: String = "",
    val productName: String = "",
    val productDescription: String = "",
    val images: List<Int> = emptyList(), // Daftar resource gambar
    val minPrice: Double = 0.0,
    val maxPrice: Double = 0.0,
    val totalStock: Int = 0,
    val storeId: String = "",
    val storeName: String = "",
    val storeLocation: String = "",
    val storeUrlLocation: String? = null,
    val mapUrl: String = "",
    // Bottom Sheet Management
    val items: List<ProductItemUiModel> = emptyList(),
    val selectedItemId: String? = null,
    val quantity: Int = 1,
    val sheetMode: SheetMode = SheetMode.NONE
)

enum class SheetMode { NONE, CART, BUY }