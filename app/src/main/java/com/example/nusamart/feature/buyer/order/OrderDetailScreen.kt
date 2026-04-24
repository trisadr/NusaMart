package com.example.nusamart.feature.buyer.order

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.entity.Order
import com.example.nusamart.entity.OrderItem
import com.example.nusamart.entity.OrderStatus
import com.example.nusamart.entity.Product
import com.example.nusamart.entity.Review
import com.example.nusamart.feature.buyer.homepage.loadProductsFromJson
import com.example.nusamart.ui.theme.NusaMartTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

// ==========================================
// DATA LOADING
// ==========================================

fun loadOrderById(context: Context, orderId: String): Order? {
    return try {
        val orderFile = File(context.filesDir, "order.json")
        val jsonString = if (orderFile.exists()) {
            orderFile.readText()
        } else {
            context.assets.open("order.json").bufferedReader().use { it.readText() }
        }
        val type = object : TypeToken<List<Order>>() {}.type
        val orders: List<Order> = Gson().fromJson(jsonString, type) ?: emptyList()
        orders.find { it.idOrder == orderId }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun loadOrderItemsByOrderId(context: Context, orderId: String): List<OrderItem> {
    return try {
        val orderItemFile = File(context.filesDir, "order_item.json")
        val jsonString = if (orderItemFile.exists()) {
            orderItemFile.readText()
        } else {
            context.assets.open("order_item.json").bufferedReader().use { it.readText() }
        }
        val type = object : TypeToken<List<OrderItem>>() {}.type
        val items: List<OrderItem> = Gson().fromJson(jsonString, type) ?: emptyList()
        items.filter { it.idOrder == orderId }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

// Cek apakah order sudah pernah direview di filesDir (writable),
// fallback ke assets jika belum ada di filesDir
fun isOrderReviewed(context: Context, orderId: String): Boolean {
    return try {
        // Cek di filesDir dulu (hasil submit review)
        val reviewFile = File(context.filesDir, "review.json")
        val jsonString = if (reviewFile.exists()) {
            reviewFile.readText()
        } else {
            // Fallback ke assets
            context.assets.open("review.json").bufferedReader().use { it.readText() }
        }
        val type = object : TypeToken<List<Review>>() {}.type
        val reviews: List<Review> = Gson().fromJson(jsonString, type) ?: emptyList()
        reviews.any { it.idOrder == orderId }
    } catch (e: Exception) {
        false
    }
}

// ==========================================
// STATEFUL SCREEN
// ==========================================

@Composable
fun OrderDetailScreen(orderId: String) {
    val context = LocalContext.current
    val backStack = LocalBackStack.current

    val order = remember { loadOrderById(context, orderId) }
    val orderItems = remember { loadOrderItemsByOrderId(context, orderId) }
    val products = remember { loadProductsFromJson(context) }
    val productMap = remember { products.associateBy { it.idProduct } }
    val isReviewed = remember { isOrderReviewed(context, orderId) }

    if (order == null) {
        Text("Order tidak ditemukan")
        return
    }

    Content(
        order = order,
        orderItems = orderItems,
        productMap = productMap,
        isReviewed = isReviewed,

        onBackClick = {
            if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex)
        },

        onReviewClick = {
            backStack.add(Routes.ReviewRoute(orderId))
        },

        onContactSellerClick = {
            // TODO: implementasi chat penjual
        }
    )
}

// ==========================================
// STATELESS CONTENT
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    order: Order,
    orderItems: List<OrderItem>,
    productMap: Map<String, Product>,
    isReviewed: Boolean,
    onBackClick: () -> Unit,
    onReviewClick: () -> Unit,
    onContactSellerClick: () -> Unit
) {
    val primaryOrange = Color(0xFFFF6D00)
    val canReview = order.status == OrderStatus.SELESAI && !isReviewed

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rincian Pesanan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (canReview) {
                        Button(
                            onClick = onReviewClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryOrange
                            )
                        ) {
                            Text(
                                "Beri Ulasan",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onContactSellerClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tanyakan Penjual", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Status pesanan
            Text(
                text = "Status Pesanan",
                color = Color.Gray,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = order.status.name,
                color = primaryOrange,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            // Badge sudah diulas
            if (order.status == OrderStatus.SELESAI && isReviewed) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✓ Sudah diulas",
                    color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Informasi pengiriman
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = primaryOrange
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Informasi Pengiriman", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "No. Resi: ${order.trackingNumber}",
                            fontSize = 13.sp
                        )
                        Text(
                            "ID Penjual: ${order.idSeller}",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            "Tanggal Order: ${order.orderDate}",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                        if (order.arrivedDate != null) {
                            Text(
                                "Tiba: ${order.arrivedDate}",
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Detail produk
            Text("Detail Produk", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.5.dp
            )

            orderItems.forEach { item ->
                val product = productMap[item.idProduct]
                OrderItemRow(item = item, product = product)
                Spacer(modifier = Modifier.height(12.dp))
            }

            HorizontalDivider(thickness = 0.5.dp)

            Spacer(modifier = Modifier.height(12.dp))

            // Total harga
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Pembayaran", fontWeight = FontWeight.Bold)
                Text(
                    "Rp ${order.totalPrice.toLong()}",
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryOrange
                )
            }

            // Catatan order
            if (order.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Catatan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    order.description,
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

// ==========================================
// ORDER ITEM ROW
// ==========================================

@Composable
private fun OrderItemRow(
    item: OrderItem,
    product: Product?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product?.name ?: item.idProduct,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${item.quantity} barang × Rp ${item.priceAtPurchase.toLong()}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Text(
            text = "Rp ${(item.quantity * item.priceAtPurchase).toLong()}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

// ==========================================
// PREVIEW
// ==========================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OrderDetailScreenPreview() {
    val dummyOrder = Order(
        idOrder = "ORD12345",
        totalPrice = 235000.0,
        status = OrderStatus.SELESAI,
        trackingNumber = "JNE123456789",
        description = "Pesanan produk kebutuhan sehari-hari",
        orderDate = 1713600000L,
        arrivedDate = null,
        idSeller = "SELLER001"
    )

    val dummyItems = listOf(
        OrderItem("OI001", "ORD12345", "PRD001", 2, 50000.0),
        OrderItem("OI002", "ORD12345", "PRD002", 1, 75000.0)
    )

    val dummyProducts = mapOf(
        "PRD001" to Product("PRD001", "Beras Makmur 5kg", 50000.0, "Beras premium", 10, "", 0, "S1", "T1", "Solo"),
        "PRD002" to Product("PRD002", "Keranjang Anyaman", 75000.0, "Keranjang kuat", 5, "", 0, "S2", "T2", "Solo")
    )

    NusaMartTheme(dynamicColor = false) {
        Content(
            order = dummyOrder,
            orderItems = dummyItems,
            productMap = dummyProducts,
            isReviewed = false,
            onBackClick = {},
            onReviewClick = {},
            onContactSellerClick = {}
        )
    }
}