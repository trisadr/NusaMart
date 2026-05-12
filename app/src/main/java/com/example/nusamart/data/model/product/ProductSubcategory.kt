package com.example.nusamart.data.model.product

data class ProductSubcategory(
    val idProductSubCat: String,    // PK
    val idProduct: String,          // FK (Product)
    val idSubCategory: String       // FK (SubCategory)
)

// class untuk menghubungkan product sama sub category - schema many to many