package com.example.nusamart.feature.buyer.homepage.home

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

class HomeVM(
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                HomeVM(
                    productRepository = app.productRepository,
                    storeRepository = app.storeRepository
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHomePageData()
    }

    private fun loadHomePageData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val allProducts = productRepository.getAllProducts()
        val allStores = storeRepository.getAllStores()

        val uiModels = allProducts.mapNotNull { product ->
            // Cari data harga dasar (Item)
            val items = productRepository.getProductItems(product.idProduct)
            if (items.isEmpty()) return@mapNotNull null // Abaikan produk yang belum punya harga/item

            // Cari gambar utama
            val images = productRepository.getProductImages(product.idProduct)
            val primaryImage = images.find { it.isPrimary }?.imageURL ?: R.drawable.nm_logo

            // Cari lokasi toko
            val store = allStores.find { it.idStore == product.idStore }
            val location = store?.location ?: "Lokasi Tidak Diketahui"

            ProductCardUiModel(
                idProduct = product.idProduct,
                name = product.productName,
                price = items.minOf { it.price }, // Ambil harga termurah jika ada variasi
                location = location,
                imageResId = primaryImage
            )
        }

        _uiState.update { it.copy(products = uiModels, isLoading = false) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}