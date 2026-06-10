package com.example.nusamart.data.repository.store

import android.content.Context
import com.example.nusamart.data.dto.StoreDto
import com.example.nusamart.data.interfaceapi.StoreApi
import com.example.nusamart.data.model.store.BadgeVerification
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

// Hasil Operasi
sealed class StoreResult {
    data class Success(val storeId: String) : StoreResult()
    data class Error(val message: String) : StoreResult()
}

// JSON Lokal (Hanya untuk Badge karena belum ada API-nya)
data class BadgeVerificationJson(
    val idBadge: String,
    val idStore: String,
    val badgeType: String,
    val requestDate: String,
    val reviewDate: String? = null,
    val endDate: String? = null,
    val status: String,
    val notes: String? = null
)

@Singleton
class StoreRepository @Inject constructor(
    private val api: StoreApi, // Inject Store API
    @ApplicationContext private val context: Context
) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val badgeFileName = "badge.json"

    // =========================================================================
    // MANAJEMEN TOKO (MENGGUNAKAN API RETROFIT)
    // =========================================================================

    suspend fun getStoreById(storeId: String): StoreDto? = withContext(Dispatchers.IO) {
        try {
            val response = api.getStoreById(storeId)
            if (response.isSuccessful) {
                return@withContext response.body()
            }
            return@withContext null
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    suspend fun getMyStore(): StoreDto? = withContext(Dispatchers.IO) {
        try {
            val response = api.getMyStore()
            if (response.isSuccessful) {
                return@withContext response.body()
            }
            return@withContext null
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    suspend fun updateStore(
        name: String,
        description: String,
        location: String,
        urlLocation: String?,
        logoURL: String? = null
    ): StoreResult = withContext(Dispatchers.IO) {
        try {
            val request = mapOf(
                "name" to name,
                "description" to description,
                "location" to location,
                "urlLocation" to urlLocation,
                "logoURL" to logoURL
            )

            val response = api.updateStore(request)

            if (response.isSuccessful) {
                val updatedStoreId = response.body()?.store?.idStore ?: ""
                return@withContext StoreResult.Success(updatedStoreId)
            }

            return@withContext StoreResult.Error("Gagal memperbarui toko.")
        } catch (e: Exception) {
            return@withContext StoreResult.Error("Gagal terhubung ke server: ${e.message}")
        }
    }

    // =========================================================================
    // MANAJEMEN BADGE VERIFIKASI (MASIH JSON LOKAL SEMENTARA)
    // =========================================================================

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
        if (json.isBlank()) return mutableListOf()
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    private fun <T> writeJson(fileName: String, data: List<T>) {
        val file = File(context.filesDir, fileName)
        file.writeText(gson.toJson(data))
    }

    suspend fun getBadgeByStoreId(storeId: String): BadgeVerificationJson? = withContext(Dispatchers.IO) {
        val badges = readJson<BadgeVerificationJson>(badgeFileName)
        return@withContext badges
            .filter { it.idStore == storeId }
            .maxByOrNull { it.requestDate }
    }

    suspend fun requestLocalBadge(storeId: String): StoreResult = withContext(Dispatchers.IO) {
        delay(500)
        val badges = readJson<BadgeVerificationJson>(badgeFileName)

        val existingActiveBadge = badges.find {
            it.idStore == storeId && (it.status == BadgeVerification.Status.PENDING.name || it.status == BadgeVerification.Status.APPROVED.name)
        }

        if (existingActiveBadge != null) {
            val statusMsg = if (existingActiveBadge.status == BadgeVerification.Status.APPROVED.name) "sudah disetujui" else "sedang diproses"
            return@withContext StoreResult.Error("Pengajuan badge toko ini $statusMsg.")
        }

        val maxIdNum = badges.maxOfOrNull { it.idBadge.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
        val newId = String.format("BDG-%06d", maxIdNum + 1)
        val now = LocalDateTime.now().toString()

        val newBadge = BadgeVerificationJson(
            idBadge = newId, idStore = storeId, badgeType = BadgeVerification.BadgeType.LOCAL.name,
            requestDate = now, reviewDate = null, endDate = null, status = BadgeVerification.Status.PENDING.name, notes = null
        )

        badges.add(newBadge)
        writeJson(badgeFileName, badges)
        return@withContext StoreResult.Success(newId)
    }

    suspend fun getAllStores(): List<StoreDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAllStores()
            if (response.isSuccessful) {
                // Jika berhasil, kembalikan list toko. Jika kosong, kembalikan list kosong.
                return@withContext response.body() ?: emptyList()
            }
            return@withContext emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }
}