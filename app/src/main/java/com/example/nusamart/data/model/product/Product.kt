package com.example.nusamart.data.model.product

data class Product(
    val idProduct: String,      // PK
    val idStore: String,        // FK (Store)
    val productName: String,
    val description: String? = null,
    val weightGram: Int,
    val productStatus: ProductStatus,
    val createAt: java.time.LocalDateTime,
    val updateAt: java.time.LocalDateTime,
    val avgRating: Double? = null,
    val sold: Int
) {
    enum class ProductStatus {
        ACTIVE,
        INACTIVE
    }
}
