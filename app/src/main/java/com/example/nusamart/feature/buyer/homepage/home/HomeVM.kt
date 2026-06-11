package com.example.nusamart.feature.buyer.homepage.home

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
class HomeVM @Inject constructor(
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHomePageData()
    }

    private fun loadHomePageData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val allStores = storeRepository.getAllStores()

        // 1. Unwrap ProductResult dari getAllProducts()
        val productsResult = productRepository.getAllProducts()
        val allProducts = if (productsResult is ProductResult.Success) {
            productsResult.data
        } else {
            emptyList()
        }

        // 2. Mapping data langsung dari ProductDto (Hasil Eager Loading Laravel)
        val uiModels = allProducts.mapNotNull { product ->
            // Langsung ambil dari properti items bawaan ProductDto
            val items = product.items ?: emptyList()
            if (items.isEmpty()) return@mapNotNull null

            // Langsung ambil dari properti images bawaan ProductDto
            val primaryImageUrl = product.images?.find { it.isPrimary == 1 }?.imageURL
            val store = allStores.find { it.idStore == product.idStore }

            ProductCardUiModel(
                idProduct = product.idProduct,
                name = product.productName,
                price = items.minOf { it.price },
                location = store?.location ?: "Lokasi Tidak Diketahui",
                imageResId = primaryImageUrl // <- Ubah ini menjadi imageUrl
            )
        }
        // Sisa blok } else { null } sudah dihapus agar strukturnya rapi

        _uiState.update { it.copy(products = uiModels, isLoading = false) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}