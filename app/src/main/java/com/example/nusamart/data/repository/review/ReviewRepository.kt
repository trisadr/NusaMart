package com.example.nusamart.data.repository.review

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

// ─── JSON-Friendly Models ─────────────────────────────────────────────────────

data class ReviewJson(
    val idReview: String,
    val idOrderItem: String,
    val idUser: String,
    val rating: Double,
    val comment: String? = null,
    val isHidden: Boolean,
    val createAt: String
)

data class ReviewImageJson(
    val idRevImage: String,
    val idReview: String,
    val urlImage: Int
)

// ─── Repository ──────────────────────────────────────────────────────────────

class ReviewRepository(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val reviewFile = "review.json"
    private val reviewImageFile = "review_image.json"

    // ─── Helper Baca/Tulis JSON ───────────────────────────────────────────────

    private inline fun <reified T> readJson(fileName: String): MutableList<T> {
        val file = File(context.filesDir, fileName)

        if (!file.exists()) {
            try {
                context.assets.open(fileName).use { inputStream ->
                    val json = inputStream.bufferedReader().readText()
                    file.writeText(json)
                }
            } catch (e: Exception) {
                return mutableListOf()
            }
        }

        val json = file.readText()
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    private fun <T> writeJson(fileName: String, data: List<T>) {
        val file = File(context.filesDir, fileName)
        file.writeText(gson.toJson(data))
    }

    // ==========================================
    // MANAJEMEN ULASAN
    // ==========================================

    // Tambahkan di dalam class ReviewRepository
    suspend fun getReviewsByItemIds(itemIds: List<String>): List<ReviewJson> = withContext(Dispatchers.IO) {
        val reviews = readJson<ReviewJson>(reviewFile)
        return@withContext reviews.filter { it.idOrderItem in itemIds }
    }
    suspend fun createReview(
        idOrderItem: String,
        idUser: String,
        rating: Double,
        comment: String?,
        imageResId: Int?
    ) = withContext(Dispatchers.IO) {
        delay(300) // Simulasi loading network
        val reviews = readJson<ReviewJson>(reviewFile)

        // Auto Increment REV-000001
        val maxRevNum = reviews.maxOfOrNull { it.idReview.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
        val newReviewId = String.format("REV-%06d", maxRevNum + 1)

        val newReview = ReviewJson(
            idReview = newReviewId,
            idOrderItem = idOrderItem,
            idUser = idUser,
            rating = rating,
            comment = comment,
            isHidden = false,
            createAt = LocalDateTime.now().toString()
        )
        reviews.add(newReview)
        writeJson(reviewFile, reviews)

        // Jika user mengupload foto ulasan
        if (imageResId != null) {
            val reviewImages = readJson<ReviewImageJson>(reviewImageFile)
            val maxImgNum = reviewImages.maxOfOrNull { it.idRevImage.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
            val newRevImageId = String.format("RVI-%06d", maxImgNum + 1)

            val newImage = ReviewImageJson(
                idRevImage = newRevImageId,
                idReview = newReviewId,
                urlImage = imageResId
            )
            reviewImages.add(newImage)
            writeJson(reviewImageFile, reviewImages)
        }
    }
}