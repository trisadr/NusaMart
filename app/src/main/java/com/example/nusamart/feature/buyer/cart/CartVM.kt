package com.example.nusamart.feature.buyer.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.repository.cart.CartRepository
import com.example.nusamart.data.repository.product.ProductRepository
import com.example.nusamart.data.repository.product.ProductResult
import com.example.nusamart.data.repository.store.StoreRepository
import com.example.nusamart.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartVM @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch { loadCart() }
    }

    fun refreshCart() {
        viewModelScope.launch { loadCart() }
    }

    private suspend fun loadCart() {
        _uiState.update { it.copy(isLoading = true) }

        val userId = userRepository.getActiveUserId()
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        // ✅ Panggil sekali saja, ambil idCart dan items dari objek yang sama
        val cartResponse = cartRepository.getCartWithItems()
        val cartItems = cartResponse.items // List<CartItemDto>

        val allStores = storeRepository.getAllStores()

        val allProductsResult = productRepository.getAllProducts()
        val allProducts = if (allProductsResult is ProductResult.Success) {
            allProductsResult.data
        } else {
            emptyList()
        }

        val productImagesMap = mutableMapOf<String, String>()

        allProducts.forEach { product ->
            val detailResult = productRepository.getProductDetail(product.idProduct)
            if (detailResult is ProductResult.Success) {
                val primaryImage = detailResult.data.images.find { it.isPrimary == 1 }?.imageURL
                if (!primaryImage.isNullOrEmpty()) {
                    productImagesMap[product.idProduct] = primaryImage
                }
            }
        }

        val uiItems = cartItems.mapNotNull { cItem ->
            // Cari produk yang memiliki item dengan idItem ini
            val matchedProduct = allProducts.firstOrNull { product ->
                val detailResult = productRepository.getProductDetail(product.idProduct)
                if (detailResult is ProductResult.Success) {
                    detailResult.data.items.any { it.idItem == cItem.idItem }
                } else false
            } ?: return@mapNotNull null

            val detailResult = productRepository.getProductDetail(matchedProduct.idProduct)
            if (detailResult !is ProductResult.Success) return@mapNotNull null

            val itemDetail = detailResult.data.items.find { it.idItem == cItem.idItem }
                ?: return@mapNotNull null

            val store = allStores.find { it.idStore == matchedProduct.idStore }
            val productImageUrl = productImagesMap[matchedProduct.idProduct]

            val cartItemModel = CartItemUiModel(
                idCartItem = cItem.idCartItem,
                idItem = cItem.idItem,
                productName = matchedProduct.productName,
                price = itemDetail.price,
                quantity = cItem.quantity,
                isChecked = cItem.isChecked,
                imageUrl = productImageUrl
            )

            val storeName = store?.name ?: "Toko Lainnya"
            val storeId = store?.idStore ?: ""

            Pair(cartItemModel, Pair(storeName, storeId))
        }

        val grouped = uiItems.groupBy { it.second }.map { entry ->
            StoreCartGroup(
                storeId = entry.key.second,
                storeName = entry.key.first,
                items = entry.value.map { it.first }
            )
        }

        val checkedItems = uiItems.map { it.first }.filter { it.isChecked == 1 }
        val totalPrice = checkedItems.sumOf { it.price * it.quantity }
        val isAllChecked = if (uiItems.isNotEmpty() && uiItems.all { it.first.isChecked == 1 }) 1 else 0

        _uiState.update {
            it.copy(
                isLoading = false,
                storeGroups = grouped,
                totalPrice = totalPrice,
                checkedCount = checkedItems.size,
                isAllChecked = isAllChecked
            )
        }
    }

    fun toggleChecked(cartItemId: String, isChecked: Int) = viewModelScope.launch {
        // ✅ Konversi Int ke Boolean untuk dikirim ke repository
        cartRepository.updateChecked(cartItemId, isChecked == 1)
        loadCart()
    }

    fun toggleAllChecked(isChecked: Int) = viewModelScope.launch {
        // ✅ Repository tidak butuh cartId, langsung kirim Boolean
        cartRepository.updateAllChecked(isChecked == 1)
        loadCart()
    }

    fun increaseQuantity(cartItemId: String, currentQuantity: Int) = viewModelScope.launch {
        cartRepository.updateQuantity(cartItemId, currentQuantity + 1)
        loadCart()
    }

    fun decreaseQuantity(cartItemId: String, currentQuantity: Int) = viewModelScope.launch {
        if (currentQuantity > 1) {
            cartRepository.updateQuantity(cartItemId, currentQuantity - 1)
            loadCart()
        }
    }

    fun deleteItem(cartItemId: String) = viewModelScope.launch {
        cartRepository.deleteItem(cartItemId)
        loadCart()
    }
}