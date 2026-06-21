package com.example.nusamart.data.repository.review

import com.example.nusamart.data.dto.ByItemsRequest
import com.example.nusamart.data.dto.ReviewDto
import com.example.nusamart.data.interfaceapi.ReviewApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    private val apiService: ReviewApi
) {

    // Mengambil ulasan berdasarkan ID Produk
    suspend fun getReviewsByProduct(productId: String): List<ReviewDto> = withContext(Dispatchers.IO) {
        try {
            apiService.getReviewsByProduct(productId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Mengambil ulasan berdasarkan list ID Order Item
    suspend fun getReviewsByItemIds(itemIds: List<String>): List<ReviewDto> = withContext(Dispatchers.IO) {
        try {
            val request = ByItemsRequest(itemIds)
            apiService.getReviewsByItems(request)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Membuat ulasan baru beserta upload gambar (jika ada)
    suspend fun createReview(
        idOrderItem: String,
        rating: Double,
        comment: String?,
        localImagePath: String?
    ): Result<ReviewDto?> = withContext(Dispatchers.IO) {
        try {
            val idItemBody = idOrderItem.toRequestBody("text/plain".toMediaTypeOrNull())
            val ratingBody = rating.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val commentBody = comment?.toRequestBody("text/plain".toMediaTypeOrNull())

            var imagePart: MultipartBody.Part? = null

            if (!localImagePath.isNullOrEmpty()) {
                val file = File(localImagePath)
                if (file.exists()) {
                    // Konversi file fisik menjadi MultipartBody
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("imageFile", file.name, requestFile)
                }
            }

            val response = apiService.createReview(idItemBody, ratingBody, commentBody, imagePart)

            Result.success(response.review)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun hideReview(reviewId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            apiService.hideReview(reviewId)
            true
        } catch (e: Exception) {
            false
        }
    }
}