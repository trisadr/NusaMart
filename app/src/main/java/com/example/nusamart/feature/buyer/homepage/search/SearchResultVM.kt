package com.example.nusamart.feature.buyer.homepage.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.R
import com.example.nusamart.core.MyApplication
import com.example.nusamart.data.repository.product.ProductRepository
import com.example.nusamart.data.repository.store.StoreRepository
import com.example.nusamart.feature.buyer.homepage.ProductCardUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchResultVM(
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                SearchResultVM(app.productRepository, app.storeRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(SearchResultUiState())
    val uiState = _uiState.asStateFlow()

    // Menyimpan data asli agar tidak perlu fetch ke database setiap kali filter diubah
    private var allProductsCache: List<ProductCardUiModel> = emptyList()

    fun initialize(initialKeyword: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, currentQuery = initialKeyword) }
        // Fetch dan Map data sama seperti HomeVM
        val allProductsRaw = productRepository.getAllProducts()
        val allStores = storeRepository.getAllStores()

        allProductsCache = allProductsRaw.mapNotNull { product ->
            val items = productRepository.getProductItems(product.idProduct)
            if (items.isEmpty()) return@mapNotNull null
            val images = productRepository.getProductImages(product.idProduct)
            val store = allStores.find { it.idStore == product.idStore }

            ProductCardUiModel(
                idProduct = product.idProduct,
                name = product.productName,
                price = items.minOf { it.price },
                location = store?.location ?: "Lokasi Tidak Diketahui",
                imageResId = images.find { it.isPrimary }?.imageURL ?: R.drawable.nm_logo
            )
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