package com.example.nusamart.data.repository.store

import android.content.Context
import com.example.nusamart.data.dto.StoreDto
import com.example.nusamart.data.interfaceapi.StoreApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// Hasil Operasi
sealed class StoreResult {
    data class Success(val storeId: String) : StoreResult()
    data class Error(val message: String) : StoreResult()
}

@Singleton
class StoreRepository @Inject constructor(
    private val api: StoreApi, // Inject Store API
    @ApplicationContext private val context: Context
) {

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