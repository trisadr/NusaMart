package com.example.nusamart.feature.buyer.order.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.data.repository.order.OrderItemJson
import com.example.nusamart.feature.buyer.order.list.mapStatusToIndonesian

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    vm: OrderDetailVM = viewModel(factory = OrderDetailVM.Factory)
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(orderId) {
        vm.loadOrderDetail(orderId)
    }

    val primaryOrange = Color(0xFFFF6D00)
    val order = uiState.order

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rincian Pesanan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            if (order != null) {
                val canReview = order.orderStatus == "DELIVERED" && !uiState.isReviewed
                Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (canReview) {
                            Button(
                                onClick = { backStack.add(Routes.ReviewRoute(orderId)) },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryOrange)
                            ) {
                                Text("Beri Ulasan", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        OutlinedButton(
                            onClick = { /* TODO: Chat Penjual */ },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Tanyakan Penjual", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryOrange)
            }
        } else if (order == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(uiState.errorMessage ?: "Terjadi kesalahan")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(text = "Status Pesanan", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                Text(
                    text = mapStatusToIndonesian(order.orderStatus),
                    color = primaryOrange, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold
                )

                if (order.orderStatus == "DELIVERED" && uiState.isReviewed) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "✓ Sudah diulas", color = Color(0xFF4CAF50), style = MaterialTheme.typography.labelMedium)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.LocalShipping, null, tint = primaryOrange)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Informasi Pengiriman", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("No. Resi: ${uiState.resiNumber}", fontSize = 13.sp)
                            Text("ID Toko: ${order.idStore}", fontSize = 13.sp, color = Color.DarkGray)
                            Text("Tanggal Order: ${order.orderDate}", fontSize = 13.sp, color = Color.DarkGray)
                            if (order.arrivedDate != null) {
                                Text("Tiba: ${order.arrivedDate}", fontSize = 13.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Detail Produk", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                uiState.orderItems.forEach { item ->
                    OrderItemRow(item = item)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                HorizontalDivider(thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Rincian Pembayaran
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Harga Produk", fontSize = 14.sp)
                    Text("Rp ${order.productTotalPrice.toLong()}", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Ongkos Kirim", fontSize = 14.sp)
                    Text("Rp ${order.shippingCost.toLong()}", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Biaya Layanan", fontSize = 14.sp)
                    Text("Rp ${order.servicePrice.toLong()}", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Pembayaran", fontWeight = FontWeight.Bold)
                    Text("Rp ${order.grandTotal.toLong()}", fontWeight = FontWeight.ExtraBold, color = primaryOrange)
                }

                if (!order.buyerNote.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Catatan", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(order.buyerNote, fontSize = 13.sp, color = Color.DarkGray)
                }
            }
        }
    }
}

@Composable
private fun OrderItemRow(item: OrderItemJson) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f)) {
            // Langsung menggunakan nameSnapshot yang direkam saat checkout!
            Text(text = item.nameSnapshot, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "${item.quantity} barang × Rp ${item.priceSnapshot.toLong()}", fontSize = 12.sp, color = Color.Gray)
        }
        Text(text = "Rp ${(item.quantity * item.priceSnapshot).toLong()}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}