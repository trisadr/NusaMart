package com.example.nusamart.feature.buyer.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.entity.Order
import com.example.nusamart.entity.OrderItem
import com.example.nusamart.entity.OrderStatus
import com.example.nusamart.entity.Product
import com.example.nusamart.feature.buyer.cart.saveNewOrder
import com.example.nusamart.feature.buyer.homepage.loadProductsFromJson
import com.example.nusamart.feature.buyer.order.loadOrderById
import com.example.nusamart.feature.buyer.order.loadOrderItemsByOrderId
import com.example.nusamart.ui.theme.NusaMartTheme

// ==========================================
// STATEFUL SCREEN
// ==========================================

@Composable
fun PaymentScreen(
    orderId: String,
    selectedPaymentMethod: String? = null
) {
    val context = LocalContext.current
    val backStack = LocalBackStack.current

    val order = remember { loadOrderById(context, orderId) }
    val orderItems = remember { loadOrderItemsByOrderId(context, orderId) }
    val products = remember { loadProductsFromJson(context) }
    val productMap = remember { products.associateBy { it.idProduct } }

    // noteText tidak di-persist ke order (belum ada fieldnya), cukup state lokal
    var noteText by remember { mutableStateOf("") }

    if (order == null) {
        Text("Order tidak ditemukan")
        return
    }

    Content(
        order = order,
        orderItems = orderItems,
        productMap = productMap,
        selectedPaymentMethod = selectedPaymentMethod,
        noteText = noteText,
        onNoteChange = { noteText = it },

        onBackClick = {
            if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex)
        },

        onSelectPaymentMethod = {
            backStack.add(Routes.PaymentConfirmationRoute(orderId))
        },

        onPlaceOrder = {
            if (selectedPaymentMethod == null) return@Content

            val paymentCode = "PAY-${(100000..999999).random()}"
            val updatedOrder = order.copy(status = OrderStatus.MENUNGGU)
            saveNewOrder(context, updatedOrder, orderItems)

            // Hapus PaymentScreen dari stack lalu navigasi ke PaymentSuccess
            backStack.removeAt(backStack.lastIndex)
            backStack.add(Routes.PaymentSuccessRoute(paymentCode, orderId))
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
    selectedPaymentMethod: String?,
    noteText: String,
    onNoteChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSelectPaymentMethod: () -> Unit,
    onPlaceOrder: () -> Unit
) {
    val shippingCost = 12000L
    val serviceFee = 3000L
    val subtotal = orderItems.sumOf { item ->
        (productMap[item.idProduct]?.price ?: 0.0) * item.quantity
    }
    val total = subtotal + shippingCost + serviceFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Tagihan",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "Rp $total",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = onPlaceOrder,
                        modifier = Modifier.width(180.dp),
                        enabled = selectedPaymentMethod != null,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Buat Pesanan")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Alamat pengiriman
            item {
                Text(
                    text = "Alamat Pengiriman",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Budi Santoso | (+62) 812-3456-789",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Jl. Merdeka No. 123, Surakarta, Jawa Tengah 57123",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Daftar produk
            item {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pesanan Kamu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                orderItems.forEach { item ->
                    val product = productMap[item.idProduct]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product?.name ?: item.idProduct,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${item.quantity} barang",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = "Rp ${((product?.price ?: 0.0) * item.quantity).toLong()}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = onNoteChange,
                    label = { Text("Pesan untuk penjual (Opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Opsi pengiriman
            item {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Opsi Pengiriman",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedCard(modifier = Modifier.fillMaxWidth(), onClick = {}) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Reguler (Rp 12.000)",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Estimasi 3-5 hari kerja",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    }
                }
            }

            // Metode pembayaran
            item {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Metode Pembayaran",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSelectPaymentMethod
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = selectedPaymentMethod ?: "Pilih Metode Pembayaran",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedPaymentMethod == null) Color.Gray
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    }
                }
                if (selectedPaymentMethod == null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "* Pilih metode pembayaran untuk melanjutkan",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Rincian pembayaran
            item {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rincian Pembayaran",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(label = "Subtotal Produk", value = "Rp ${subtotal.toLong()}")
                DetailRow(label = "Subtotal Pengiriman", value = "Rp $shippingCost")
                DetailRow(label = "Biaya Layanan", value = "Rp $serviceFee")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Pembayaran",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Rp $total",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// ==========================================
// PREVIEW
// ==========================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PaymentScreenPreview() {
    val dummyOrder = Order(
        idOrder = "ORD001",
        totalPrice = 188000.0,
        status = OrderStatus.MENUNGGU,
        trackingNumber = "",
        description = "",
        orderDate = 1713600000L,
        arrivedDate = null,
        idSeller = "SELLER001"
    )
    val dummyItems = listOf(
        OrderItem("OI001", "ORD001", "PROD-001", 2, 75000.0),
        OrderItem("OI002", "ORD001", "PROD-002", 1, 38000.0)
    )
    val dummyProducts = mapOf(
        "PROD-001" to Product("PROD-001", "Beras Raja Lele 5kg", 75000.0, "", 50, "", 0, "SELL-001", "STORE-001", "Solo"),
        "PROD-002" to Product("PROD-002", "Minyak Goreng Sunco 2L", 38000.0, "", 30, "", 0, "SELL-002", "STORE-002", "Solo")
    )

    NusaMartTheme(dynamicColor = false) {
        Content(
            order = dummyOrder,
            orderItems = dummyItems,
            productMap = dummyProducts,
            selectedPaymentMethod = "Transfer Bank - BCA",
            noteText = "",
            onNoteChange = {},
            onBackClick = {},
            onSelectPaymentMethod = {},
            onPlaceOrder = {}
        )
    }
}