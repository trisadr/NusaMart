package com.example.nusamart.data.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("idProduct") val idProduct: String,
    @SerializedName("idStore") val idStore: String,
    @SerializedName("productName") val productName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("weightGram") val weightGram: Int,
    @SerializedName("productStatus") val productStatus: String,
    @SerializedName("createAt") val createAt: String?,
    @SerializedName("updateAt") val updateAt: String?,
    @SerializedName("sold") val sold: Int = 0,
    @SerializedName("avgRating") val avgRating: Double? = 0.0,
    @SerializedName("soldCount") val soldCount: Int? = 0,
    @SerializedName("product_items") val items: List<ProductItemDto>? = null,
    @SerializedName("product_images") val images: List<ProductImageDto>? = null
)

data class ProductItemDto(
    @SerializedName("idItem") val idItem: String,
    @SerializedName("idProduct") val idProduct: String,
    @SerializedName("sku") val sku: String?,
    @SerializedName("stock") val stock: Int,
    @SerializedName("price") val price: Double,
    @SerializedName("isActive") val isActive: Int,
    @SerializedName("variations") val variations: List<ProductVariationDto>? = null,
    @SerializedName("product") val product: ProductDto? = null
)

data class ProductVariationDto(
    @SerializedName("idVariation") val idVariation: String,
    @SerializedName("idItem") val idItem: String,
    @SerializedName("typeVariation") val typeVariation: String,
    @SerializedName("value") val value: String
)

data class ProductImageDto(
    @SerializedName("idImage") val idImage: String,
    @SerializedName("idProduct") val idProduct: String,
    @SerializedName("imageURL") val imageURL: String,
    @SerializedName("isPrimary") val isPrimary: Int
)

data class CategoryDto(
    @SerializedName("idCategory") val idCategory: String,
    @SerializedName("categoryName") val categoryName: String,
    @SerializedName("isActive") val isActive: Boolean
)

data class SubCategoryDto(
    @SerializedName("idSubCategory") val idSubCategory: String,
    @SerializedName("idCategory") val idCategory: String,
    @SerializedName("subCategoryName") val subCategoryName: String,
    @SerializedName("description") val description: String?
)

data class ProductDetailResponse(
    @SerializedName("product") val product: ProductDto,
    @SerializedName("items") val items: List<ProductItemDto>,
    @SerializedName("images") val images: List<ProductImageDto>,
    @SerializedName("categories") val categories: List<SubCategoryDto>
)