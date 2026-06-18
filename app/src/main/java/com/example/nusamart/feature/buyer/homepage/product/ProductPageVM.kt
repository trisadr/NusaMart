package com.example.nusamart.feature.buyer.homepage.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.BuildConfig
import com.example.nusamart.data.repository.cart.CartRepository
import com.example.nusamart.data.repository.chat.ChatRepository
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
class ProductPageVM @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductPageUiState())
    val uiState = _uiState.asStateFlow()

    fun loadProduct(productId: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, productId = productId) }

        val detailResult = productRepository.getProductDetail(productId)

        if (detailResult is ProductResult.Success) {
            val detailData = detailResult.data
            val product = detailData.product
            val itemsData = detailData.items
            val imagesData = detailData.images

            val stores = storeRepository.getAllStores()
            val store = stores.find { it.idStore == product.idStore }

            val sName = store?.name ?: "Toko Tidak Diketahui"
            val sLoc = store?.location ?: "Lokasi Tidak Diketahui"
            val sUrl = store?.urlLocation
            val isVerified = store?.isVerified == true

            val images = imagesData
                .sortedByDescending { it.isPrimary }
                .map { "${BuildConfig.STORAGE_URL}${it.imageURL}" }

            val uiItems = itemsData.map { item ->
                val variations = item.variations ?: emptyList()
                val varName = if (variations.isEmpty()) "Default"
                else variations.joinToString(" - ") { it.value }

                ProductItemUiModel(
                    idItem = item.idItem,
                    price = item.price,
                    stock = item.stock,
                    variationName = varName
                )
            }.filter { it.stock > 0 }

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
                    soldCount = product.soldCount ?: 0, // <-- Mengambil data terjual dari DTO
                    storeId = product.idStore,
                    storeName = sName,
                    storeLocation = sLoc,
                    storeUrlLocation = sUrl,
                    isStoreVerified = isVerified,
                    items = uiItems,
                    selectedItemId = firstSelectedId,
                    quantity = 1
                )
            }
        } else {
            _uiState.update { it.copy(isLoading = false) }
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

        cartRepository.addCartItem(state.selectedItemId, state.quantity)

        closeSheet()
        onSuccess("${state.quantity} ${state.productName} masuk ke keranjang")
    }

    fun startChatWithSeller(onNavigateToChat: (String) -> Unit) {
        viewModelScope.launch {
            val myId = userRepository.getActiveUserId()
            val currentStoreId = _uiState.value.storeId

            val stores = storeRepository.getAllStores()
            val store = stores.find { it.idStore == currentStoreId }
            val sellerId = store?.idSeller

            if (myId != null && sellerId != null) {
                val room = chatRepository.getOrCreateRoom(sellerId)
                room?.let { onNavigateToChat(it.idRoom) }
            }
        }
    }
}