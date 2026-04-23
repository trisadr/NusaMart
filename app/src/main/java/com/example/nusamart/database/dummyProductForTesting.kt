package com.example.nusamart.database

import com.example.nusamart.entity.Product

object dummyProductForTesting {

    val products = listOf(
        Product(
            idProduct = "PROD-001",
            name = "Beras Raja Lele 5kg",
            price = 75000.0,
            description = "Beras pulen berkualitas tinggi dari petani lokal.",
            stock = 50,
            map = "",
            imageResId = android.R.drawable.ic_menu_gallery,
            idSeller = "SELL-001",
            idStore = "STORE-001",
            location = "Jebres, Surakarta"
        ),
        Product(
            idProduct = "PROD-002",
            name = "Minyak Goreng Sunco 2L",
            price = 38000.0,
            description = "Minyak goreng bening, tidak mudah beku.",
            stock = 30,
            map = "",
            imageResId = android.R.drawable.ic_menu_gallery,
            idSeller = "SELL-002",
            idStore = "STORE-002",
            location = "Banjarsari, Surakarta"
        )
    )

    fun getProductById(id: String): Product? {
        return products.find { it.idProduct == id }
    }
}