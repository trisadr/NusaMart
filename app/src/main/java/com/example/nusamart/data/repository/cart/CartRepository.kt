package com.example.nusamart.data.repository.cart

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

// ─── JSON-Friendly Models ─────────────────────────────────────────────────────

data class CartJson(
    val idCart: String,
    val idUser: String
)

data class CartItemJson(
    val idCartItem: String,
    val idCart: String,
    val idItem: String,
    val quantity: Int,
    val isChecked: Boolean
)

// ─── Repository ──────────────────────────────────────────────────────────────

class CartRepository(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val cartFile = "cart.json"
    private val cartItemFile = "cart_item.json"

    // ─── Helper Baca/Tulis JSON ───────────────────────────────────────────────

    private inline fun <reified T> readJson(fileName: String): MutableList<T> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            try {
                context.assets.open(fileName).use { inputStream ->
                    val json = inputStream.bufferedReader().readText()
                    file.writeText(json)
                }
            } catch (e: Exception) {
                return mutableListOf()
            }
        }
        val json = file.readText()
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    private fun <T> writeJson(fileName: String, data: List<T>) {
        val file = File(context.filesDir, fileName)
        file.writeText(gson.toJson(data))
    }

    // ==========================================
    // MANAJEMEN KERANJANG & ITEM
    // ==========================================

    suspend fun getOrCreateCart(userId: String): CartJson = withContext(Dispatchers.IO) {
        val carts = readJson<CartJson>(cartFile)
        val existingCart = carts.find { it.idUser == userId }

        if (existingCart != null) {
            return@withContext existingCart
        }

        // Buat keranjang baru jika belum ada
        val newCartId = "CRT-${UUID.randomUUID().toString().take(6).uppercase()}"
        val newCart = CartJson(idCart = newCartId, idUser = userId)
        carts.add(newCart)
        writeJson(cartFile, carts)

        return@withContext newCart
    }

    suspend fun getCartItems(cartId: String): List<CartItemJson> = withContext(Dispatchers.IO) {
        val items = readJson<CartItemJson>(cartItemFile)
        return@withContext items.filter { it.idCart == cartId }
    }

    suspend fun updateQuantity(cartItemId: String, newQuantity: Int) = withContext(Dispatchers.IO) {
        val items = readJson<CartItemJson>(cartItemFile)
        val index = items.indexOfFirst { it.idCartItem == cartItemId }
        if (index != -1) {
            items[index] = items[index].copy(quantity = newQuantity)
            writeJson(cartItemFile, items)
        }
    }

    suspend fun updateChecked(cartItemId: String, isChecked: Boolean) = withContext(Dispatchers.IO) {
        val items = readJson<CartItemJson>(cartItemFile)
        val index = items.indexOfFirst { it.idCartItem == cartItemId }
        if (index != -1) {
            items[index] = items[index].copy(isChecked = isChecked)
            writeJson(cartItemFile, items)
        }
    }

    suspend fun updateAllChecked(cartId: String, isChecked: Boolean) = withContext(Dispatchers.IO) {
        val items = readJson<CartItemJson>(cartItemFile)
        for (i in items.indices) {
            if (items[i].idCart == cartId) {
                items[i] = items[i].copy(isChecked = isChecked)
            }
        }
        writeJson(cartItemFile, items)
    }

    suspend fun deleteItem(cartItemId: String) = withContext(Dispatchers.IO) {
        val items = readJson<CartItemJson>(cartItemFile)
        items.removeAll { it.idCartItem == cartItemId }
        writeJson(cartItemFile, items)
    }

    // Tambahkan di dalam CartRepository
    suspend fun addCartItem(cartId: String, itemId: String, quantity: Int) = withContext(Dispatchers.IO) {
        val items = readJson<CartItemJson>(cartItemFile)

        // Cek apakah item sudah ada di keranjang
        val existingItem = items.find { it.idCart == cartId && it.idItem == itemId }
        if (existingItem != null) {
            // Jika ada, tambahkan quantity-nya
            val index = items.indexOf(existingItem)
            items[index] = existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {
            // Jika belum, buat baru
            val maxNum = items.maxOfOrNull { it.idCartItem.substringAfterLast("-").toIntOrNull() ?: 0 } ?: 0
            val newItem = CartItemJson(
                idCartItem = "CRI-${maxNum + 1}",
                idCart = cartId,
                idItem = itemId,
                quantity = quantity,
                isChecked = true
            )
            items.add(newItem)
        }
        writeJson(cartItemFile, items)
    }
}