package com.example.nusamart.feature.buyer.homepage.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.BuildConfig
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
class StorePageVM @Inject constructor(
    private val storeRepository: StoreRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StorePageUiState())
    val uiState = _uiState.asStateFlow()

    fun initialize(storeId: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, storeId = storeId) }

        val storeInfo = storeRepository.getStoreById(storeId)

        val productsResult = productRepository.getAllProducts()
        val storeProductsRaw = if (productsResult is ProductResult.Success) {
            productsResult.data.filter { it.idStore == storeId }
        } else {
            emptyList()
        }

        val mappedProducts = storeProductsRaw.mapNotNull { product ->
            val detailResult = productRepository.getProductDetail(product.idProduct)

            if (detailResult is ProductResult.Success) {
                val productDetail = detailResult.data
                val items = productDetail.items

                if (items.isEmpty()) return@mapNotNull null

                val primaryImageUrl = productDetail.images.find { it.isPrimary == 1 }?.imageURL?.let { "${BuildConfig.STORAGE_URL}$it" }

                ProductCardUiModel(
                    idProduct = product.idProduct,
                    name = product.productName,
                    price = items.minOf { it.price },
                    location = storeInfo?.location ?: "Lokasi Tidak Diketahui",
                    imageResId = primaryImageUrl
                )
            } else {
                null
            }
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                storeName = storeInfo?.name ?: "Toko Tidak Diketahui",
                storeLocation = storeInfo?.location ?: "Lokasi Tidak Diketahui",
                storeRating = storeInfo?.storeRating ?: 0.0,
                isVerified = storeInfo?.isVerified == true,
                storeProducts = mappedProducts
            )
        }
    }
}