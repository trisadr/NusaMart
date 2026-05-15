package com.example.nusamart.data.repository.store

import android.content.Context
import com.example.nusamart.data.model.store.BadgeVerification
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

// JSON-Friendly Models

data class StoreJson(
    val idStore: String,
    val idSeller: String,
    val name: String,
    val description: String,
    val logoURL: Int? = null,
    val location: String,
    val urlLocation: String? = null,
    val createAt: String,
    val updateAt: String,
    val storeRating: Double? = null,
    val isActive: Boolean
)

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

// Hasil Operasi

sealed class StoreResult {
    data class Success(val storeId: String) : StoreResult()
    data class Error(val message: String) : StoreResult()
}

// Repository

class StoreRepository(private val context: Context) {

    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    private val storeFileName = "store.json"
    private val badgeFileName = "badge.json"

    // Helper Baca/Tulis JSON

    private inline fun <reified T> readJson(fileName: String): MutableList<T> {

        val file = File(context.filesDir, fileName)

        // Jika file belum ada, maka copy dari assets
        if (!file.exists()) {
            try {
                context.assets.open(fileName).use { inputStream ->
                    val json = inputStream
                        .bufferedReader()
                        .readText()
                    file.writeText(json)
                }
            } catch (e: Exception) {
                return mutableListOf()
            }
        }
        val json = file.readText()
        // Hindari crash kalau file kosong
        if (json.isBlank()) {
            return mutableListOf()
        }
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(json, type)
            ?: mutableListOf()
    }

    private fun <T> writeJson(
        fileName: String,
        data: List<T>
    ) {
        val file = File(context.filesDir, fileName)
        file.writeText(
            gson.toJson(data)
        )
    }


    // MANAJEMEN TOKO (STORE)

    suspend fun getAllStores(): List<StoreJson> =
        withContext(Dispatchers.IO) {

            return@withContext readJson<StoreJson>(storeFileName)
                .filter { it.isActive }
        }

    suspend fun getStoreBySellerId(
        sellerId: String
    ): StoreJson? = withContext(Dispatchers.IO) {

        val stores = readJson<StoreJson>(storeFileName)

        return@withContext stores.find {
            it.idSeller == sellerId
        }
    }

    suspend fun getStoreById(
        storeId: String
    ): StoreJson? = withContext(Dispatchers.IO) {

        val stores = readJson<StoreJson>(storeFileName)

        return@withContext stores.find {
            it.idStore == storeId
        }
    }

    suspend fun createStore(
        sellerId: String,
        name: String,
        description: String,
        location: String,
        urlLocation: String?
    ): StoreResult = withContext(Dispatchers.IO) {

        delay(500)

        val stores = readJson<StoreJson>(storeFileName)

        // Cek seller sudah punya toko
        if (stores.any { it.idSeller == sellerId }) {

            return@withContext StoreResult.Error(
                "Penjual ini sudah memiliki toko."
            )
        }

        // Cek nama toko sudah dipakai
        if (stores.any {
                it.name.equals(name, ignoreCase = true)
            }) {

            return@withContext StoreResult.Error(
                "Nama toko \"$name\" sudah digunakan."
            )
        }

        // Auto Increment STR-000001
        val maxIdNum = stores.maxOfOrNull {

            it.idStore
                .substringAfter("-")
                .toIntOrNull() ?: 0

        } ?: 0

        val newId = String.format(
            "STR-%06d",
            maxIdNum + 1
        )

        val now = LocalDateTime.now().toString()

        val newStore = StoreJson(
            idStore = newId,
            idSeller = sellerId,
            name = name,
            description = description,
            logoURL = null,
            location = location,
            urlLocation = urlLocation,
            createAt = now,
            updateAt = now,
            storeRating = null,
            isActive = true
        )

        stores.add(newStore)

        writeJson(
            storeFileName,
            stores
        )

        return@withContext StoreResult.Success(newId)
    }

    suspend fun updateStore(
        storeId: String,
        name: String,
        description: String,
        location: String,
        urlLocation: String?,
        isActive: Boolean
    ): StoreResult = withContext(Dispatchers.IO) {
        val stores = readJson<StoreJson>(storeFileName)
        val index = stores.indexOfFirst {
            it.idStore == storeId
        }
        if (index == -1) {
            return@withContext StoreResult.Error(
                "Toko tidak ditemukan."
            )
        }
        // Cek nama kembar
        if (stores.any {
                it.name.equals(
                    name,
                    ignoreCase = true
                ) && it.idStore != storeId
            }) {
            return@withContext StoreResult.Error(
                "Nama toko \"$name\" sudah digunakan."
            )
        }
        stores[index] = stores[index].copy(
            name = name,
            description = description,
            location = location,
            urlLocation = urlLocation,
            isActive = isActive,
            updateAt = LocalDateTime.now().toString()
        )
        writeJson(
            storeFileName,
            stores
        )
        return@withContext StoreResult.Success(storeId)
    }


    // MANAJEMEN BADGE VERIFIKASI

    suspend fun getBadgeByStoreId(
        storeId: String
    ): BadgeVerificationJson? = withContext(Dispatchers.IO) {

        val badges = readJson<BadgeVerificationJson>(
            badgeFileName
        )

        return@withContext badges
            .filter {
                it.idStore == storeId
            }
            .maxByOrNull {
                it.requestDate
            }
    }

    suspend fun requestLocalBadge(
        storeId: String
    ): StoreResult = withContext(Dispatchers.IO) {
        delay(500)
        val badges = readJson<BadgeVerificationJson>(
            badgeFileName
        )
        // Cek badge aktif
        val existingActiveBadge = badges.find {
            it.idStore == storeId &&
                    (
                            it.status ==
                                    BadgeVerification.Status.PENDING.name ||

                                    it.status ==
                                    BadgeVerification.Status.APPROVED.name
                            )
        }
        if (existingActiveBadge != null) {
            val statusMsg =
                if (
                    existingActiveBadge.status ==
                    BadgeVerification.Status.APPROVED.name
                ) {
                    "sudah disetujui"
                } else {
                    "sedang diproses"
                }
            return@withContext StoreResult.Error(
                "Pengajuan badge toko ini $statusMsg."
            )
        }

        // Auto Increment BDG-000001
        val maxIdNum = badges.maxOfOrNull {
            it.idBadge
                .substringAfter("-")
                .toIntOrNull() ?: 0
        } ?: 0
        val newId = String.format(
            "BDG-%06d",
            maxIdNum + 1
        )
        val now = LocalDateTime.now().toString()
        val newBadge = BadgeVerificationJson(
            idBadge = newId,
            idStore = storeId,
            badgeType = BadgeVerification.BadgeType.LOCAL.name,
            requestDate = now,
            reviewDate = null,
            endDate = null,
            status = BadgeVerification.Status.PENDING.name,
            notes = null
        )
        badges.add(newBadge)
        writeJson(
            badgeFileName,
            badges
        )
        return@withContext StoreResult.Success(newId)
    }
}