package com.example.nusamart.data.repository.shipping

import com.example.nusamart.data.dto.AddTrackingRequest
import com.example.nusamart.data.dto.CourierOptionDto
import com.example.nusamart.data.dto.CreateShippingRequest
import com.example.nusamart.data.dto.ShippingDto
import com.example.nusamart.data.dto.ShippingTrackingDto
import com.example.nusamart.data.dto.UpdateShippingStatusRequest
import com.example.nusamart.data.interfaceapi.ShippingApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// Sealed class untuk menangkap hasil operasi (sukses/gagal)
sealed class ShippingResult {
    data class Success(val data: Any? = null) : ShippingResult()
    data class Error(val message: String) : ShippingResult()
}

@Singleton
class ShippingRepository @Inject constructor(
    private val apiService: ShippingApi
) {

    // --- MANAJEMEN KURIR ---

    suspend fun getActiveCouriers(): List<CourierOptionDto> = withContext(Dispatchers.IO) {
        try {
            val couriers = apiService.getCouriers()
            // Filter isActive == 1 karena menggunakan Int (0/1) dari MySQL
            couriers.filter { it.isActive == 1 }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCourierById(courierId: String): CourierOptionDto? = withContext(Dispatchers.IO) {
        try {
            apiService.getCourierDetail(courierId)
        } catch (e: Exception) {
            null
        }
    }


    // --- MANAJEMEN PENGIRIMAN ---

    suspend fun getShippingByOrderId(orderId: String): ShippingDto? = withContext(Dispatchers.IO) {
        try {
            apiService.getShippingByOrder(orderId)
        } catch (e: Exception) {
            null
        }
    }

    // Dipanggil saat seller mengonfirmasi pesanan
    suspend fun createShipping(orderId: String, courierId: String): ShippingResult = withContext(Dispatchers.IO) {
        try {
            val request = CreateShippingRequest(idOrder = orderId, idCourier = courierId)
            val response = apiService.createShipping(request)

            // Kembalikan ID shipping yang baru terbuat
            ShippingResult.Success(response.shipping?.idShipping)
        } catch (e: Exception) {
            ShippingResult.Error(e.message ?: "Gagal membuat data pengiriman")
        }
    }

    // Dipanggil saat seller menginput resi atau kurir mengupdate status
    suspend fun updateShippingStatus(
        shippingId: String,
        newStatus: String, // String status seperti "PICKED_UP", "IN_TRANSIT"
        resiNumber: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = UpdateShippingStatusRequest(shippingStatus = newStatus, resi = resiNumber)
            apiService.updateShippingStatus(shippingId, request)
            true
        } catch (e: Exception) {
            false
        }
    }


    // --- RIWAYAT PELACAKAN (TRACKING) ---

    suspend fun getTrackingHistory(shippingId: String): List<ShippingTrackingDto> = withContext(Dispatchers.IO) {
        try {
            apiService.getTrackingHistory(shippingId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Menambah log pergerakan paket
    suspend fun addTrackingUpdate(
        shippingId: String,
        location: String?,
        description: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = AddTrackingRequest(packetLocation = location, description = description)
            apiService.addTrackingUpdate(shippingId, request)
            true
        } catch (e: Exception) {
            false
        }
    }
}