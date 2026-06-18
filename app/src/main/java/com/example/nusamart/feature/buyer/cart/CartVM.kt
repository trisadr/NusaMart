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

        // Hanya 3x Request API di awal, sangat cepat!
        val cartResponse = cartRepository.getCartWithItems()
        val cartItems = cartResponse.items

        val allStores = storeRepository.getAllStores()
        val allProductsResult = productRepository.getAllProducts()
        val allProducts = if (allProductsResult is ProductResult.Success) allProductsResult.data else emptyList()

        // 🚀 ULTRA SPEED: Tidak ada pemanggilan API getProductDetail di sini!
        // Langsung mapping dari data allProducts karena items dan images sudah disediakan DTO
        val uiItems = cartItems.mapNotNull { cItem ->

            // 1. Cari produk yang memiliki ID Item keranjang ini
            val matchedProduct = allProducts.find { product ->
                product.items?.any { it.idItem == cItem.idItem } == true
            } ?: return@mapNotNull null

            // 2. Ambil harga dan gambar langsung dari dalam objek produk
            val itemDetail = matchedProduct.items?.find { it.idItem == cItem.idItem } ?: return@mapNotNull null
            val primaryImage = matchedProduct.images?.find { it.isPrimary == 1 }?.imageURL
                ?: matchedProduct.images?.firstOrNull()?.imageURL

            val store = allStores.find { it.idStore == matchedProduct.idStore }

            val cartItemModel = CartItemUiModel(
                idCartItem = cItem.idCartItem,
                idItem = cItem.idItem,
                productName = matchedProduct.productName,
                price = itemDetail.price,
                quantity = cItem.quantity,
                isChecked = cItem.isChecked,
                imageUrl = primaryImage
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

        _uiState.update { state ->
            recalculateState(state.copy(isLoading = false, storeGroups = grouped))
        }
    }

    private fun recalculateState(state: CartUiState): CartUiState {
        val allItems = state.storeGroups.flatMap { it.items }
        val checkedItems = allItems.filter { it.isChecked == 1 }
        val totalPrice = checkedItems.sumOf { it.price * it.quantity }
        val isAllChecked = if (allItems.isNotEmpty() && checkedItems.size == allItems.size) 1 else 0

        return state.copy(
            totalPrice = totalPrice,
            checkedCount = checkedItems.size,
            isAllChecked = isAllChecked
        )
    }

    // =======================================================================
    // OPTIMISTIC UI: UI Berubah Instan, API berjalan mulus di Background
    // =======================================================================

    fun toggleChecked(cartItemId: String, isChecked: Int) {
        _uiState.update { state ->
            val newGroups = state.storeGroups.map { group ->
                group.copy(items = group.items.map { item ->
                    if (item.idCartItem == cartItemId) item.copy(isChecked = isChecked) else item
                })
            }
            recalculateState(state.copy(storeGroups = newGroups))
        }
        viewModelScope.launch { cartRepository.updateChecked(cartItemId, isChecked == 1) }
    }

    fun toggleAllChecked(isChecked: Int) {
        _uiState.update { state ->
            val newGroups = state.storeGroups.map { group ->
                group.copy(items = group.items.map { it.copy(isChecked = isChecked) })
            }
            recalculateState(state.copy(storeGroups = newGroups))
        }
        viewModelScope.launch { cartRepository.updateAllChecked(isChecked == 1) }
    }

    fun increaseQuantity(cartItemId: String, currentQuantity: Int) {
        val newQty = currentQuantity + 1
        _uiState.update { state ->
            val newGroups = state.storeGroups.map { group ->
                group.copy(items = group.items.map { item ->
                    if (item.idCartItem == cartItemId) item.copy(quantity = newQty) else item
                })
            }
            recalculateState(state.copy(storeGroups = newGroups))
        }
        viewModelScope.launch { cartRepository.updateQuantity(cartItemId, newQty) }
    }

    fun decreaseQuantity(cartItemId: String, currentQuantity: Int) {
        if (currentQuantity <= 1) return
        val newQty = currentQuantity - 1
        _uiState.update { state ->
            val newGroups = state.storeGroups.map { group ->
                group.copy(items = group.items.map { item ->
                    if (item.idCartItem == cartItemId) item.copy(quantity = newQty) else item
                })
            }
            recalculateState(state.copy(storeGroups = newGroups))
        }
        viewModelScope.launch { cartRepository.updateQuantity(cartItemId, newQty) }
    }

    fun deleteItem(cartItemId: String) {
        _uiState.update { state ->
            val newGroups = state.storeGroups.map { group ->
                group.copy(items = group.items.filterNot { it.idCartItem == cartItemId })
            }.filter { it.items.isNotEmpty() }
            recalculateState(state.copy(storeGroups = newGroups))
        }
        viewModelScope.launch { cartRepository.deleteItem(cartItemId) }
    }
}