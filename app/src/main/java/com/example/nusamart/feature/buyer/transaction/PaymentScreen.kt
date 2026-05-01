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
import com.example.nusamart.feature.buyer.cart.clearPendingOrder
import com.example.nusamart.feature.buyer.cart.loadCartItems
import com.example.nusamart.feature.buyer.cart.loadPendingOrder
import com.example.nusamart.feature.buyer.cart.saveCartItems
import com.example.nusamart.feature.buyer.cart.saveNewOrder
import com.example.nusamart.feature.buyer.homepage.loadProductsFromJson
import com.example.nusamart.ui.theme.NusaMartTheme
import java.util.UUID

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun PaymentScreen(
    orderId: String? = null,
    productId: String? = null,
    quantity: Int = 1,
    fromCart: Boolean = true,
    selectedPaymentMethod: String? = null
) {
    val context = LocalContext.current
    val backStack = LocalBackStack.current
    val products = remember { loadProductsFromJson(context) }
    val productMap = remember { products.associateBy { it.idProduct } }

    // Dari CartScreen: baca pending order yang disimpan CartScreen
    val pendingOrder = remember { if (fromCart) loadPendingOrder(context) else null }

    // Dari ProductPage: bangun order items sementara dari productId + quantity
    val directProduct = remember(productId) {
        if (!fromCart && productId != null) productMap[productId] else null
    }
    val directOrderItems: List<OrderItem> = remember(productId, quantity) {
        if (!fromCart && directProduct != null) {
            listOf(
                OrderItem(
                    idOrderItem = "TEMP-OI",
                    idOrder = "TEMP",
                    idProduct = productId!!,
                    quantity = quantity,
                    priceAtPurchase = directProduct.price
                )
            )
        } else emptyList()
    }
    val activeOrderItems = if (fromCart) {
        pendingOrder?.orderItems ?: emptyList()
    } else {
        directOrderItems
    }
    if (fromCart && pendingOrder == null) {
        Text("Order tidak ditemukan")
        return
    }
    if (!fromCart && directProduct == null) {
        Text("Produk tidak ditemukan")
        return
    }

    var noteText by remember { mutableStateOf("") }

    Content(
        orderItems = activeOrderItems,
        productMap = productMap,
        selectedPaymentMethod = selectedPaymentMethod,
        noteText = noteText,
        onNoteChange = { noteText = it },

        onBackClick = {
            if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex)
        },

        onSelectPaymentMethod = {
            backStack.add(
                Routes.PaymentConfirmationRoute(
                    orderId   = orderId,
                    productId = productId,
                    quantity  = quantity,
                    fromCart  = fromCart
                )
            )
        },

        onPlaceOrder = {
            if (selectedPaymentMethod == null) return@Content

            if (fromCart) {
                saveNewOrder(context, pendingOrder!!.order, pendingOrder.orderItems)
                val cartIdsToDelete = pendingOrder.cartIdsToDelete.toSet()
                val currentCart = loadCartItems(context)
                val updatedCart = currentCart.filter { it.idCart !in cartIdsToDelete }
                saveCartItems(context, updatedCart)
                clearPendingOrder(context)

                val paymentCode = "PAY-${(100000..999999).random()}"
                backStack.removeAt(backStack.lastIndex)
                backStack.add(Routes.PaymentSuccessRoute(paymentCode, pendingOrder.order.idOrder))

            } else {
                val newOrderId = "ORD-${UUID.randomUUID().toString().take(8).uppercase()}"

                val newOrder = Order(
                    idOrder = newOrderId,
                    totalPrice = directProduct!!.price * quantity,
                    status = OrderStatus.MENUNGGU,
                    trackingNumber = "",
                    description = "",
                    orderDate = System.currentTimeMillis(),
                    arrivedDate = null,
                    idSeller = directProduct.idSeller
                )
                val newOrderItem = OrderItem(
                    idOrderItem = "OI-$newOrderId-1",
                    idOrder = newOrderId,
                    idProduct = productId!!,
                    quantity = quantity,
                    priceAtPurchase = directProduct.price
                )
                saveNewOrder(context, newOrder, listOf(newOrderItem))

                val paymentCode = "PAY-${(100000..999999).random()}"
                backStack.removeAt(backStack.lastIndex)
                backStack.add(Routes.PaymentSuccessRoute(paymentCode, newOrderId))
            }
        }
    )
}

// ─── Content ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
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
                        Text(text = "Total Tagihan", style = MaterialTheme.typography.labelLarge)
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
                OutlinedCard(modifier = Modifier.fillMaxWidth(), onClick = onSelectPaymentMethod) {
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

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PaymentScreenPreview() {
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