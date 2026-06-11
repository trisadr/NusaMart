package com.example.nusamart.data.repository.order

import com.example.nusamart.data.dto.CreateOrderRequest
import com.example.nusamart.data.dto.OrderDto
import com.example.nusamart.data.dto.OrderItemDto
import com.example.nusamart.data.dto.OrderItemRequest
import com.example.nusamart.data.dto.UpdateOrderStatusRequest
import com.example.nusamart.data.interfaceapi.OrderApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// Dipakai oleh CheckoutVM untuk input item saat createOrder
data class OrderItemInput(
    val idItem: String,
    val quantity: Int,
    val nameSnapshot: String,
    val priceSnapshot: Double
)

sealed class OrderResult {
    data class Success(val orderId: String) : OrderResult()
    data class Error(val message: String) : OrderResult()
}

@Singleton
class OrderRepository @Inject constructor(
    private val apiService: OrderApi
) {

    suspend fun getOrdersByUser(): List<OrderDto> = withContext(Dispatchers.IO) {
        try {
            apiService.getOrders()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSellerOrders(): List<OrderDto> = withContext(Dispatchers.IO) {
        try {
            apiService.getSellerOrders()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getOrderById(orderId: String): OrderDto? = withContext(Dispatchers.IO) {
        try {
            apiService.getOrderById(orderId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getOrderItems(orderId: String): List<OrderItemDto> = withContext(Dispatchers.IO) {
        try {
            // Items sudah di-eager load dalam OrderDto.orderItems
            apiService.getOrderById(orderId).orderItems
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createOrder(
        userId: String,   // tidak dipakai, token sudah handle auth — dibiarkan agar tidak breaking change
        storeId: String,
        addressId: String,
        paymentId: String,
        items: List<OrderItemInput>,
        shippingCost: Double,
        servicePrice: Double,
        buyerNote: String? = null
    ): OrderResult = withContext(Dispatchers.IO) {
        try {
            val request = CreateOrderRequest(
                idStore = storeId,
                idAddress = addressId,
                idPayment = paymentId,
                shippingCost = shippingCost,
                servicePrice = servicePrice,
                buyerNote = buyerNote,
                items = items.map {
                    OrderItemRequest(
                        idItem = it.idItem,
                        quantity = it.quantity,
                        nameSnapshot = it.nameSnapshot,
                        priceSnapshot = it.priceSnapshot
                    )
                }
            )
            val response = apiService.createOrder(request)
            val orderId = response.order?.idOrder
                ?: return@withContext OrderResult.Error("Order ID tidak ditemukan")
            OrderResult.Success(orderId)
        } catch (e: Exception) {
            OrderResult.Error(e.message ?: "Gagal membuat pesanan")
        }
    }

    suspend fun cancelOrder(orderId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            apiService.cancelOrder(orderId)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = UpdateOrderStatusRequest(orderStatus = newStatus)
            apiService.updateOrderStatus(orderId, request)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun isOrderReviewed(orderId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            apiService.isReviewed(orderId).isReviewed
        } catch (e: Exception) {
            false
        }
    }
}