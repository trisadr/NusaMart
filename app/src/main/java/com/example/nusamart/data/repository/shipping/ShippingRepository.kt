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

sealed class ShippingResult {
    data class Success(val data: Any? = null) : ShippingResult()
    data class Error(val message: String) : ShippingResult()
}

@Singleton
class ShippingRepository @Inject constructor(
    private val apiService: ShippingApi
) {
    suspend fun getActiveCouriers(): List<CourierOptionDto> = withContext(Dispatchers.IO) {
        try {
            val couriers = apiService.getCouriers()
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


    suspend fun getShippingByOrderId(orderId: String): ShippingDto? = withContext(Dispatchers.IO) {
        try {
            apiService.getShippingByOrder(orderId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createShipping(orderId: String, courierId: String): ShippingResult = withContext(Dispatchers.IO) {
        try {
            val request = CreateShippingRequest(idOrder = orderId, idCourier = courierId)
            val response = apiService.createShipping(request)

            ShippingResult.Success(response.shipping?.idShipping)
        } catch (e: Exception) {
            ShippingResult.Error(e.message ?: "Gagal membuat data pengiriman")
        }
    }

    suspend fun updateShippingStatus(
        shippingId: String,
        newStatus: String,
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

    suspend fun getTrackingHistory(shippingId: String): List<ShippingTrackingDto> = withContext(Dispatchers.IO) {
        try {
            apiService.getTrackingHistory(shippingId)
        } catch (e: Exception) {
            emptyList()
        }
    }

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