package com.example.nusamart.data.model.product

data class Category(
    val idCategory: String,     // PK
    val categoryName: String,
    val iconURL: Int? = null,
    val isActive: Boolean
)
