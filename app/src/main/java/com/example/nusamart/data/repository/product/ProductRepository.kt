package com.example.nusamart.data.repository.product

import com.example.nusamart.data.dto.CategoryDto
import com.example.nusamart.data.dto.ProductDetailResponse
import com.example.nusamart.data.dto.ProductDto
import com.example.nusamart.data.dto.SubCategoryDto
import com.example.nusamart.data.interfaceapi.ProductApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class ProductResult<out T> {
    data class Success<out T>(val data: T) : ProductResult<T>()
    data class Error(val message: String) : ProductResult<Nothing>()
}

@Singleton
class ProductRepository @Inject constructor(
    private val apiService: ProductApi
) {
    suspend fun getAllCategories(): ProductResult<List<CategoryDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getCategories()
            ProductResult.Success(response)
        } catch (e: Exception) {
            ProductResult.Error(e.localizedMessage ?: "Terjadi kesalahan saat mengambil kategori")
        }
    }

    suspend fun getSubCategoriesByCategory(categoryId: String): ProductResult<List<SubCategoryDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getSubCategories(categoryId)
            ProductResult.Success(response)
        } catch (e: Exception) {
            ProductResult.Error(e.localizedMessage ?: "Terjadi kesalahan")
        }
    }

    suspend fun getAllProducts(): ProductResult<List<ProductDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getAllProducts()
            ProductResult.Success(response)
        } catch (e: Exception) {
            ProductResult.Error(e.localizedMessage ?: "Gagal memuat produk")
        }
    }

    suspend fun getProductDetail(productId: String): ProductResult<ProductDetailResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getProductDetail(productId)
            ProductResult.Success(response)
        } catch (e: Exception) {
            ProductResult.Error(e.localizedMessage ?: "Gagal memuat detail produk")
        }
    }

    suspend fun getProductsByStore(storeId: String): ProductResult<List<ProductDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getProductsByStore(storeId)
            ProductResult.Success(response)
        } catch (e: Exception) {
            ProductResult.Error(e.localizedMessage ?: "Gagal memuat produk toko")
        }
    }

    suspend fun searchProducts(keyword: String): ProductResult<List<ProductDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.searchProducts(keyword)
            ProductResult.Success(response)
        } catch (e: Exception) {
            ProductResult.Error(e.localizedMessage ?: "Gagal mencari produk")
        }
    }
}