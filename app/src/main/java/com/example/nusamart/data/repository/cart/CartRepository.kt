package com.example.nusamart.data.repository.cart

import com.example.nusamart.data.dto.AddCartItemRequest
import com.example.nusamart.data.dto.CartResponse
import com.example.nusamart.data.dto.UpdateCheckedRequest
import com.example.nusamart.data.dto.UpdateQuantityRequest
import com.example.nusamart.data.interfaceapi.CartApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val apiService: CartApi
) {

    // Mengambil ID Cart dan seluruh isinya sekaligus
    suspend fun getCartWithItems(): CartResponse = withContext(Dispatchers.IO) {
        apiService.getCart()
    }

    suspend fun addCartItem(idItem: String, quantity: Int) = withContext(Dispatchers.IO) {
        val request = AddCartItemRequest(idItem, quantity)
        apiService.addItem(request)
    }

    suspend fun updateQuantity(cartItemId: String, quantity: Int) = withContext(Dispatchers.IO) {
        val request = UpdateQuantityRequest(quantity)
        apiService.updateQuantity(cartItemId, request)
    }

    suspend fun updateChecked(cartItemId: String, isChecked: Boolean) = withContext(Dispatchers.IO) {
        val request = UpdateCheckedRequest(isChecked)
        apiService.updateChecked(cartItemId, request)
    }

    suspend fun updateAllChecked(isChecked: Boolean) = withContext(Dispatchers.IO) {
        val request = UpdateCheckedRequest(isChecked)
        apiService.updateAllChecked(request)
    }

    suspend fun deleteItem(cartItemId: String) = withContext(Dispatchers.IO) {
        apiService.deleteItem(cartItemId)
    }
}