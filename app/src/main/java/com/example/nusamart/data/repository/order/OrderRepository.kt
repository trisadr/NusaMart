package com.example.nusamart.data.repository.order

import android.content.Context
import com.example.nusamart.data.model.order.Order
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class OrderJson(
    val idOrder: String,
    val idUser: String,
    val idStore: String,
    val idAddress: String,
    val invoiceNumber: String,
    val orderDate: String,
    val arrivedDate: String? = null,
    val orderStatus: String,
    val productTotalPrice: Double,
    val shippingCost: Double,
    val servicePrice: Double,
    val grandTotal: Double,
    val buyerNote: String? = null,
    val createAt: String,
    val updateAt: String
)

data class OrderItemJson(
    val idOrderItem: String,
    val idOrder: String,
    val idItem: String,
    val quantity: Int,
    val nameSnapshot: String,
    val priceSnapshot: Double
)

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

class OrderRepository(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private val orderFile = "order.json"
    private val orderItemFile = "order_item.json"

    private inline fun <reified T> readJson(fileName: String): MutableList<T> {
        val file = File(context.filesDir, fileName)

        if (!file.exists()) {
            try {
                context.assets.open(fileName).use { inputStream ->
                    val json = inputStream.bufferedReader().readText()
                    file.writeText(json) // Salin dari assets ke internal storage
                }
            } catch (e: Exception) {
                // Jika di assets juga tidak ada, baru kembalikan list kosong
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

   // Mengambil semua pesanan milik seorang Pembeli (Buyer)
    suspend fun getOrdersByUser(userId: String): List<OrderJson> = withContext(Dispatchers.IO) {
        val orders = readJson<OrderJson>(orderFile)
        return@withContext orders.filter { it.idUser == userId }
            .sortedByDescending { it.createAt } // Urutkan dari yang terbaru
    }

    // Mengambil semua pesanan yang masuk ke sebuah Toko (Seller)
    suspend fun getOrdersByStore(storeId: String): List<OrderJson> = withContext(Dispatchers.IO) {
        val orders = readJson<OrderJson>(orderFile)
        return@withContext orders.filter { it.idStore == storeId }
            .sortedByDescending { it.createAt }
    }

    // Mengambil detail satu pesanan spesifik
    suspend fun getOrderById(orderId: String): OrderJson? = withContext(Dispatchers.IO) {
        val orders = readJson<OrderJson>(orderFile)
        return@withContext orders.find { it.idOrder == orderId }
    }

    // Mengambil daftar barang (items) di dalam satu pesanan
    suspend fun getOrderItems(orderId: String): List<OrderItemJson> = withContext(Dispatchers.IO) {
        val orderItems = readJson<OrderItemJson>(orderItemFile)
        return@withContext orderItems.filter { it.idOrder == orderId }
    }

    suspend fun createOrder(
        userId: String,
        storeId: String,
        addressId: String,
        items: List<OrderItemInput>,
        shippingCost: Double,
        servicePrice: Double,
        buyerNote: String? = null
    ): OrderResult = withContext(Dispatchers.IO) {
        delay(1000)

        if (items.isEmpty()) {
            return@withContext OrderResult.Error("Pesanan gagal dibuat: Tidak ada barang yang dibeli.")
        }

        val orders = readJson<OrderJson>(orderFile)
        val orderItems = readJson<OrderItemJson>(orderItemFile)

        val productTotalPrice = items.sumOf { it.priceSnapshot * it.quantity }
        val grandTotal = productTotalPrice + shippingCost + servicePrice

        // Generate Order ID (ORD-000001)
        val maxOrdNum = orders.maxOfOrNull { it.idOrder.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
        val newOrderId = String.format("ORD-%06d", maxOrdNum + 1)

        // Generate Invoice Number (INV/YYYYMMDD/ORD-XXXXXX)
        val todayStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val invoiceNumber = "INV/$todayStr/$newOrderId"
        val now = LocalDateTime.now().toString()

        val newOrder = OrderJson(
            idOrder = newOrderId,
            idUser = userId,
            idStore = storeId,
            idAddress = addressId,
            invoiceNumber = invoiceNumber,
            orderDate = now,
            arrivedDate = null,
            orderStatus = Order.OrderStatus.PENDING.name,
            productTotalPrice = productTotalPrice,
            shippingCost = shippingCost,
            servicePrice = servicePrice,
            grandTotal = grandTotal,
            buyerNote = buyerNote,
            createAt = now,
            updateAt = now
        )
        orders.add(newOrder)
        writeJson(orderFile, orders)

        var maxOitNum = orderItems.maxOfOrNull { it.idOrderItem.substringAfter("-").toIntOrNull() ?: 0 } ?: 0

        items.forEach { inputItem ->
            maxOitNum++
            val newOrderItem = OrderItemJson(
                idOrderItem = String.format("OIT-%06d", maxOitNum),
                idOrder = newOrderId,
                idItem = inputItem.idItem,
                quantity = inputItem.quantity,
                nameSnapshot = inputItem.nameSnapshot,
                priceSnapshot = inputItem.priceSnapshot
            )
            orderItems.add(newOrderItem)
        }
        writeJson(orderItemFile, orderItems)

        return@withContext OrderResult.Success(newOrderId)
    }

    suspend fun updateOrderStatus(
        orderId: String,
        newStatus: Order.OrderStatus
    ): Boolean = withContext(Dispatchers.IO) {
        val orders = readJson<OrderJson>(orderFile)
        val index = orders.indexOfFirst { it.idOrder == orderId }

        if (index != -1) {
            val nowStr = LocalDateTime.now().toString()
            val isDelivered = newStatus == Order.OrderStatus.DELIVERED

            orders[index] = orders[index].copy(
                orderStatus = newStatus.name,
                updateAt = nowStr,
                arrivedDate = if (isDelivered) nowStr else orders[index].arrivedDate
            )

            writeJson(orderFile, orders)
            return@withContext true
        }
        return@withContext false
    }

    suspend fun isOrderReviewed(orderId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(context.filesDir, "review.json")
            val json = if (file.exists()) {
                file.readText()
            } else {
                context.assets.open("review.json").bufferedReader().readText()
            }

            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            val reviews: List<Map<String, Any>> = gson.fromJson(json, type) ?: emptyList()

            return@withContext reviews.any { it["idOrder"] == orderId }
        } catch (e: Exception) {
            return@withContext false
        }
    }
}