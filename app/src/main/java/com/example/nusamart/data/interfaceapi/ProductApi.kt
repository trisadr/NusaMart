package com.example.nusamart.data.interfaceapi

import com.example.nusamart.data.dto.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    @GET("api/products")
    suspend fun getAllProducts(): List<ProductDto>

    @GET("api/products/{id}")
    suspend fun getProductDetail(@Path("id") id: String): ProductDetailResponse

    @GET("api/categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("api/categories/{id}/subcategories")
    suspend fun getSubCategories(@Path("id") categoryId: String): List<SubCategoryDto>

    @GET("api/products/store/{storeId}")
    suspend fun getProductsByStore(@Path("storeId") storeId: String): List<ProductDto>

    @GET("api/products/search")
    suspend fun searchProducts(@Query("q") keyword: String): List<ProductDto>

    // Catatan: Anda perlu menambahkan endpoint POST di Laravel Controller
    // jika ingin mengaktifkan fitur tambah produk dari aplikasi.
}