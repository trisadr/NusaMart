package com.example.nusamart.data.dto

import com.google.gson.annotations.SerializedName

// --- RESPONSE (Balasan dari Server) ---

data class CartResponse(
    @SerializedName("idCart") val idCart: String,
    @SerializedName("items") val items: List<CartItemDto>
)

data class CartItemDto(
    @SerializedName("idCartItem") val idCartItem: String,
    @SerializedName("idCart") val idCart: String,
    @SerializedName("idItem") val idItem: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("isChecked") val isChecked: Int, // Gunakan Int (0/1) jika belum pakai $casts di Laravel

    // Hasil Eager Loading dari Laravel (productItem)
    @SerializedName("product_item") val productItem: ProductItemDto? = null
)

data class CartActionResponse(
    @SerializedName("message") val message: String,
    @SerializedName("cartItem") val cartItem: CartItemDto?
)

data class GeneralResponse(
    @SerializedName("message") val message: String
)


// --- REQUEST (Data yang dikirim ke Server) ---

data class AddCartItemRequest(
    @SerializedName("idItem") val idItem: String,
    @SerializedName("quantity") val quantity: Int
)

data class UpdateQuantityRequest(
    @SerializedName("quantity") val quantity: Int
)

data class UpdateCheckedRequest(
    @SerializedName("isChecked") val isChecked: Boolean // Boolean aman dikirim, Laravel akan paham
)