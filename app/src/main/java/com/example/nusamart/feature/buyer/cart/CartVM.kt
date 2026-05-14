package com.example.nusamart.feature.buyer.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.R
import com.example.nusamart.core.MyApplication
import com.example.nusamart.data.repository.cart.CartRepository
import com.example.nusamart.data.repository.product.ProductItemJson
import com.example.nusamart.data.repository.product.ProductJson
import com.example.nusamart.data.repository.product.ProductRepository
import com.example.nusamart.data.repository.store.StoreRepository
import com.example.nusamart.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartVM(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                CartVM(app.cartRepository, app.productRepository, app.storeRepository, app.userRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()

    // Menyimpan ID keranjang aktif untuk keperluan toggle all
    private var activeCartId: String? = null

    init {
        viewModelScope.launch {
            loadCart()
        }
    }

    // Tambahkan kata 'suspend' dan HAPUS '= viewModelScope.launch'
    private suspend fun loadCart() {
        _uiState.update { it.copy(isLoading = true) }
        val userId = userRepository.getActiveUserId()

        if (userId == null) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        // 1. Dapatkan keranjang dan isinya
        val cart = cartRepository.getOrCreateCart(userId)
        activeCartId = cart.idCart
        val cartItems = cartRepository.getCartItems(cart.idCart)

        // 2. Load semua data referensi
        val allProducts = productRepository.getAllProducts()
        val allStores = storeRepository.getAllStores()

        val itemMap = mutableMapOf<String, Pair<ProductJson, ProductItemJson>>()
        allProducts.forEach { product ->
            val productItems = productRepository.getProductItems(product.idProduct)
            productItems.forEach { item ->
                itemMap[item.idItem] = Pair(product, item)
            }
        }

        // 3. Gabungkan data
        val uiItems = cartItems.mapNotNull { cItem ->
            val productData = itemMap[cItem.idItem] ?: return@mapNotNull null
            val product = productData.first
            val itemPrice = productData.second.price

            val store = allStores.find { it.idStore == product.idStore }
            val images = productRepository.getProductImages(product.idProduct)
            val primaryImage = images.find { it.isPrimary }?.imageURL ?: R.drawable.nm_logo

            val cartItemModel = CartItemUiModel(
                idCartItem = cItem.idCartItem,
                idItem = cItem.idItem,
                productName = product.productName,
                price = itemPrice,
                quantity = cItem.quantity,
                isChecked = cItem.isChecked,
                imageResId = primaryImage
            )

            val storeName = store?.name ?: "Toko Lainnya"
            val storeId = store?.idStore ?: ""

            Pair(cartItemModel, Pair(storeName, storeId))
        }

        // 4. Group berdasarkan toko
        val grouped = uiItems.groupBy { it.second }.map { entry ->
            val storeData = entry.key
            val itemsInStore = entry.value

            StoreCartGroup(
                storeId = storeData.second,
                storeName = storeData.first,
                items = itemsInStore.map { it.first }
            )
        }

        val checkedItems = uiItems.map { it.first }.filter { it.isChecked }
        val totalPrice = checkedItems.sumOf { it.price * it.quantity }
        val isAllChecked = uiItems.isNotEmpty() && uiItems.all { it.first.isChecked }

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

    fun toggleChecked(cartItemId: String, isChecked: Boolean) = viewModelScope.launch {
        cartRepository.updateChecked(cartItemId, isChecked)
        loadCart()
    }

    fun toggleAllChecked(isChecked: Boolean) = viewModelScope.launch {
        activeCartId?.let {
            cartRepository.updateAllChecked(it, isChecked)
            loadCart()
        }
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