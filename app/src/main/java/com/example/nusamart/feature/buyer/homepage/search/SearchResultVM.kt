package com.example.nusamart.feature.buyer.homepage.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.repository.product.ProductRepository
import com.example.nusamart.data.repository.product.ProductResult
import com.example.nusamart.data.repository.store.StoreRepository
import com.example.nusamart.feature.buyer.homepage.ProductCardUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultVM @Inject constructor(
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchResultUiState())
    val uiState = _uiState.asStateFlow()

    // Menyimpan data asli agar tidak perlu fetch ke database setiap kali filter diubah
    private var allProductsCache: List<ProductCardUiModel> = emptyList()

    fun initialize(initialKeyword: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, currentQuery = initialKeyword) }

        val allStores = storeRepository.getAllStores()

        // 1. Unwrap ProductResult dari getAllProducts()
        val productsResult = productRepository.getAllProducts()
        val allProductsRaw = if (productsResult is ProductResult.Success) {
            productsResult.data
        } else {
            emptyList()
        }

        // 2. Gunakan getProductDetail untuk memetakan data
        allProductsCache = allProductsRaw.mapNotNull { product ->

            val detailResult = productRepository.getProductDetail(product.idProduct)

            if (detailResult is ProductResult.Success) {
                val productDetail = detailResult.data
                val items = productDetail.items

                // Abaikan produk yang belum punya harga/item
                if (items.isEmpty()) return@mapNotNull null

                // Ambil URL gambar utama
                val primaryImageUrl = productDetail.images.find { it.isPrimary == 1 }?.imageURL

                // Cari lokasi toko
                val store = allStores.find { it.idStore == product.idStore }

                ProductCardUiModel(
                    idProduct = product.idProduct,
                    name = product.productName,
                    price = items.minOf { it.price },
                    location = store?.location ?: "Lokasi Tidak Diketahui",
                    imageResId = primaryImageUrl // Pastikan menggunakan imageUrl, bukan imageResId
                )
            } else {
                null // Jika gagal fetch detail, lewati produk ini
            }
        }

        applyFilters()
        _uiState.update { it.copy(isLoading = false) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(currentQuery = query) }
        applyFilters()
    }

    fun updateFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        val baseList = allProductsCache.filter {
            it.name.contains(state.currentQuery, ignoreCase = true)
        }
        val sortedList = when (state.selectedFilter) {
            "Harga Termurah" -> baseList.sortedBy { it.price }
            "Harga Termahal" -> baseList.sortedByDescending { it.price }
            else -> baseList
        }
        _uiState.update { it.copy(filteredProducts = sortedList) }
    }
}