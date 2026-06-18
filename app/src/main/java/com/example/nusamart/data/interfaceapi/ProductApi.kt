package com.example.nusamart.data.interfaceapi

import com.example.nusamart.data.dto.CategoryDto
import com.example.nusamart.data.dto.ProductDetailResponse
import com.example.nusamart.data.dto.ProductDto
import com.example.nusamart.data.dto.SubCategoryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    @GET("products")
    suspend fun getAllProducts(): List<ProductDto>

    @GET("products/{id}")
    suspend fun getProductDetail(@Path("id") id: String): ProductDetailResponse

    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("categories/{id}/subcategories")
    suspend fun getSubCategories(@Path("id") categoryId: String): List<SubCategoryDto>

    @GET("products/store/{storeId}")
    suspend fun getProductsByStore(@Path("storeId") storeId: String): List<ProductDto>

    @GET("products/search")
    suspend fun searchProducts(@Query("q") keyword: String): List<ProductDto>

}