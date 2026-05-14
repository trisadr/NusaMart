package com.example.nusamart.data.model.product

data class SubCategory(
    val idSubCategory: String,  // PK
    val idCategory: String,     // FK (Category)
    val subCategoryName: String,
    val description: String? = null
)
