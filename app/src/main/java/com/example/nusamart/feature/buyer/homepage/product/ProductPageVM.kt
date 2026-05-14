package com.example.nusamart.feature.buyer.homepage.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.R
import com.example.nusamart.core.MyApplication
import com.example.nusamart.data.repository.cart.CartRepository
import com.example.nusamart.data.repository.product.ProductRepository
import com.example.nusamart.data.repository.store.StoreRepository
import com.example.nusamart.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductPageVM(
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                ProductPageVM(app.productRepository, app.storeRepository, app.cartRepository, app.userRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(ProductPageUiState())
    val uiState = _uiState.asStateFlow()

    fun loadProduct(productId: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, productId = productId) }

        val allProducts = productRepository.getAllProducts()
        val product = allProducts.find { it.idProduct == productId } ?: return@launch

        // 1. Ambil Data Store
        val stores = storeRepository.getAllStores()
        val store = stores.find { it.idStore == product.idStore }
        val sName = store?.name ?: "Toko Tidak Diketahui"
        val sLoc = store?.location ?: "Lokasi Tidak Diketahui"

        // 2. Ambil & Urutkan Gambar (Primary di urutan 0)
        val images = productRepository.getProductImages(productId)
            .sortedByDescending { it.isPrimary }
            .map { it.imageURL }
            .ifEmpty { listOf(R.drawable.nm_logo) }

        // 3. Ambil Item & Variasi
        val itemsData = productRepository.getProductItems(productId)
        val uiItems = itemsData.map { item ->
            val variations = productRepository.getProductVariations(item.idItem)
            val varName = if (variations.isEmpty()) "Default" else variations.joinToString(" - ") { it.value }

            ProductItemUiModel(
                idItem = item.idItem,
                price = item.price,
                stock = item.stock,
                variationName = varName
            )
        }.filter { it.stock > 0 } // Hanya tampilkan yang stoknya ada

        val minPrice = uiItems.minOfOrNull { it.price } ?: 0.0
        val maxPrice = uiItems.maxOfOrNull { it.price } ?: 0.0
        val totalStock = uiItems.sumOf { it.stock }
        val firstSelectedId = uiItems.firstOrNull()?.idItem

        _uiState.update {
            it.copy(
                isLoading = false,
                productName = product.productName,
                productDescription = product.description ?: "Tidak ada deskripsi",
                images = images,
                minPrice = minPrice,
                maxPrice = maxPrice,
                totalStock = totalStock,
                storeId = product.idStore,
                storeName = sName,
                storeLocation = sLoc,
                items = uiItems,
                selectedItemId = firstSelectedId,
                quantity = 1
            )
        }
    }

    // --- Bottom Sheet Actions ---
    fun openSheet(mode: SheetMode) = _uiState.update { it.copy(sheetMode = mode, quantity = 1) }
    fun closeSheet() = _uiState.update { it.copy(sheetMode = SheetMode.NONE) }

    fun selectItem(itemId: String) = _uiState.update { it.copy(selectedItemId = itemId, quantity = 1) }

    fun increaseQuantity() = _uiState.update { state ->
        val selectedItem = state.items.find { it.idItem == state.selectedItemId }
        val maxStock = selectedItem?.stock ?: 1
        if (state.quantity < maxStock) state.copy(quantity = state.quantity + 1) else state
    }

    fun decreaseQuantity() = _uiState.update {
        if (it.quantity > 1) it.copy(quantity = it.quantity - 1) else it
    }

    fun addToCart(onSuccess: (String) -> Unit) = viewModelScope.launch {
        val state = _uiState.value
        if (state.selectedItemId == null) return@launch

        val userId = userRepository.getActiveUserId() ?: return@launch
        val cart = cartRepository.getOrCreateCart(userId)

        cartRepository.addCartItem(cart.idCart, state.selectedItemId, state.quantity)

        closeSheet()
        onSuccess("${state.quantity} ${state.productName} masuk ke keranjang")
    }
}