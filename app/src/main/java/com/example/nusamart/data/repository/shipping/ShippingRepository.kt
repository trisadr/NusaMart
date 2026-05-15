package com.example.nusamart.data.repository.shipping

import android.content.Context
import com.example.nusamart.data.model.shipping.Shipping
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

// JSON-Friendly Models

data class CourierOptionJson(
    val idCourier: String,
    val courierName: String,
    val serviceType: String,
    val timeEstimation: String,
    val isActive: Boolean
)

data class ShippingJson(
    val idShipping: String,
    val idOrder: String,
    val idCourier: String,
    val resi: String? = null,
    val shippingDate: String? = null,
    val deliveredDate: String? = null,
    val shippingStatus: String
)

data class ShippingTrackingJson(
    val idTracking: String,
    val idShipping: String,
    val packetLocation: String? = null,
    val description: String? = null,
    val updateAt: String
)

// Hasil Operasi

sealed class ShippingResult {
    data class Success(val shippingId: String) : ShippingResult()
    data class Error(val message: String) : ShippingResult()
}

// Repository

class ShippingRepository(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private val courierFile = "courier_option.json"
    private val shippingFile = "shipping.json"
    private val trackingFile = "shipping_tracking.json"

    // Helper Baca/Tulis JSON

    private inline fun <reified T> readJson(fileName: String): MutableList<T> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            // Coba salin dari assets jika file belum ada di internal storage (misal untuk data kurir)
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


    // MANAJEMEN KURIR (COURIER)

    suspend fun getActiveCouriers(): List<CourierOptionJson> = withContext(Dispatchers.IO) {
        val couriers = readJson<CourierOptionJson>(courierFile)
        return@withContext couriers.filter { it.isActive }
    }

    suspend fun getCourierById(courierId: String): CourierOptionJson? = withContext(Dispatchers.IO) {
        val couriers = readJson<CourierOptionJson>(courierFile)
        return@withContext couriers.find { it.idCourier == courierId }
    }


    // MANAJEMEN PENGIRIMAN (SHIPPING)

    suspend fun getShippingByOrderId(orderId: String): ShippingJson? = withContext(Dispatchers.IO) {
        val shippings = readJson<ShippingJson>(shippingFile)
        return@withContext shippings.find { it.idOrder == orderId }
    }

    // Dipanggil saat seller mengonfirmasi/memproses pesanan
    suspend fun createShipping(
        orderId: String,
        courierId: String
    ): ShippingResult = withContext(Dispatchers.IO) {
        delay(500)
        val shippings = readJson<ShippingJson>(shippingFile)

        // Cek apakah order sudah memiliki data pengiriman
        if (shippings.any { it.idOrder == orderId }) {
            return@withContext ShippingResult.Error("Data pengiriman untuk pesanan ini sudah dibuat.")
        }

        // Auto Increment SHP-000001
        val maxShpNum = shippings.maxOfOrNull { it.idShipping.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
        val newShippingId = String.format("SHP-%06d", maxShpNum + 1)

        val newShipping = ShippingJson(
            idShipping = newShippingId,
            idOrder = orderId,
            idCourier = courierId,
            resi = null,
            shippingDate = null,
            deliveredDate = null,
            shippingStatus = Shipping.ShippingStatus.WAITING.name
        )

        shippings.add(newShipping)
        writeJson(shippingFile, shippings)

        return@withContext ShippingResult.Success(newShippingId)
    }

    // Dipanggil saat seller menginput resi atau kurir mengupdate status
    suspend fun updateShippingStatus(
        shippingId: String,
        newStatus: Shipping.ShippingStatus,
        resiNumber: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        val shippings = readJson<ShippingJson>(shippingFile)
        val index = shippings.indexOfFirst { it.idShipping == shippingId }

        if (index != -1) {
            val nowStr = LocalDateTime.now().toString()
            val oldShipping = shippings[index]

            // Set shippingDate jika status berubah menjadi PICKED_UP
            val newShippingDate = if (newStatus == Shipping.ShippingStatus.PICKED_UP && oldShipping.shippingDate == null) {
                nowStr
            } else {
                oldShipping.shippingDate
            }

            // Set deliveredDate jika status berubah menjadi DELIVERED
            val newDeliveredDate = if (newStatus == Shipping.ShippingStatus.DELIVERED && oldShipping.deliveredDate == null) {
                nowStr
            } else {
                oldShipping.deliveredDate
            }

            shippings[index] = oldShipping.copy(
                shippingStatus = newStatus.name,
                resi = resiNumber ?: oldShipping.resi,
                shippingDate = newShippingDate,
                deliveredDate = newDeliveredDate
            )

            writeJson(shippingFile, shippings)
            return@withContext true
        }
        return@withContext false
    }


    // RIWAYAT PELACAKAN (TRACKING)

    suspend fun getTrackingHistory(shippingId: String): List<ShippingTrackingJson> = withContext(Dispatchers.IO) {
        val trackings = readJson<ShippingTrackingJson>(trackingFile)
        // Urutkan dari yang terbaru (updateAt descending)
        return@withContext trackings.filter { it.idShipping == shippingId }
            .sortedByDescending { it.updateAt }
    }

    // Menambah log pergerakan paket
    suspend fun addTrackingUpdate(
        shippingId: String,
        location: String?,
        description: String
    ): Boolean = withContext(Dispatchers.IO) {
        val trackings = readJson<ShippingTrackingJson>(trackingFile)

        // Auto Increment TRK-000001
        val maxTrkNum = trackings.maxOfOrNull { it.idTracking.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
        val newTrackingId = String.format("TRK-%06d", maxTrkNum + 1)

        val newTracking = ShippingTrackingJson(
            idTracking = newTrackingId,
            idShipping = shippingId,
            packetLocation = location,
            description = description,
            updateAt = LocalDateTime.now().toString()
        )

        trackings.add(newTracking)
        writeJson(trackingFile, trackings)

        return@withContext true
    }
}