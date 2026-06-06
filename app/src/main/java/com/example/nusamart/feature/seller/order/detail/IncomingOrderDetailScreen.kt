package com.example.nusamart.feature.seller.order.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nusamart.core.LocalBackStack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomingOrderDetailScreen(
    orderId: String,
    vm: IncomingOrderDetailVM = hiltViewModel()
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(orderId) { vm.loadOrderDetail(orderId) }

    val order = uiState.order
    val buyer = uiState.buyer

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pesanan", fontWeight = FontWeight.Bold) },
                // TOMBOL BACK DI KANAN ATAS
                actions = {
                    IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            if (order != null) {
                Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        when (order.orderStatus) {
                            "PENDING" -> {
                                // Tombol Batalkan (Outlined)
                                OutlinedButton(
                                    onClick = { vm.cancelOrder(order.idOrder) },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Batalkan", fontWeight = FontWeight.Bold)
                                }
                                // Tombol Proses (Filled)
                                Button(
                                    onClick = {
                                        // Asumsi order memiliki field idCourier. Ganti dengan "CUR-001" jika tidak ada.
                                        vm.processOrder(order.idOrder, "CUR-001")
                                    },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Proses Pesanan", fontWeight = FontWeight.Bold)
                                }
                            }
                            "PROCESSED" -> {
                                // Tombol Kirim Pesanan
                                Button(
                                    onClick = { vm.shipOrder(order.idOrder) },
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Kirim Pesanan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                            // Jika SHIPPED, DELIVERED, atau CANCELLED tidak perlu tombol aksi
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (order == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Data tidak ditemukan") }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Info Status & ID Order
                Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Receipt, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ID Pesanan: ${order.idOrder}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Status: ${mapStatusToIndo(order.orderStatus)}",
                            fontWeight = FontWeight.ExtraBold,
                            color = getStatusColor(order.orderStatus)
                        )
                        Text(text = "Tanggal: ${order.orderDate}", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Info Pembeli
                Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text("Informasi Pembeli", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = buyer?.username ?: order.idUser, fontWeight = FontWeight.SemiBold)
                        }
                        Text(text = buyer?.phone ?: "-", fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(start = 28.dp, top = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Daftar Barang
                Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text("Daftar Produk", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        uiState.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "${item.quantity}x ${item.nameSnapshot}", modifier = Modifier.weight(1f))
                                Text(text = "Rp ${(item.priceSnapshot * item.quantity).toLong()}", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Rincian Harga
                Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Harga Barang")
                            Text("Rp ${order.grandTotal.toLong()}")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Ongkos Kirim")
                            Text("Rp ${order.shippingCost.toLong()}")
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pendapatan Bersih", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Rp ${order.grandTotal.toLong()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Helper Functions
fun mapStatusToIndo(status: String): String {
    return when (status) {
        "PENDING" -> "MENUNGGU PROSES"
        "PROCESSED" -> "SEDANG DIPROSES"
        "SHIPPED" -> "DALAM PENGIRIMAN"
        "DELIVERED" -> "SELESAI"
        "CANCELLED" -> "DIBATALKAN"
        else -> status
    }
}

@Composable
fun getStatusColor(status: String): Color {
    return when (status) {
        "SELESAI" -> MaterialTheme.colorScheme.primary
        "SHIPPED" -> MaterialTheme.colorScheme.tertiary
        "PROCESSED" -> MaterialTheme.colorScheme.secondary
        "PENDING" -> MaterialTheme.colorScheme.outline
        "CANCELLED" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}