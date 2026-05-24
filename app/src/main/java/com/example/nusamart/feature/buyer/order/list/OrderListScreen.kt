package com.example.nusamart.feature.buyer.order.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(vm: OrderListVM = viewModel(factory = OrderListVM.Factory)) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    // Memastikan daftar order di-refresh setiap kali layar dibuka
    LaunchedEffect(Unit) {
        vm.loadOrders()
    }

    val primaryOrange = Color(0xFFFF6D00)
    val filterOptions = listOf("Semua", "MENUNGGU", "DIPROSES", "DIKIRIM", "SELESAI", "DIBATALKAN")

    // Filter berdasarkan status dari model `uiState.orders.order`
    val filteredOrders = remember(uiState.selectedFilter, uiState.orders) {
        if (uiState.selectedFilter == "Semua") {
            uiState.orders
        } else {
            uiState.orders.filter { mapStatusToIndonesian(it.order.orderStatus) == uiState.selectedFilter }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pesanan Saya", fontWeight = FontWeight.ExtraBold, color = primaryOrange) },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFAFAFA))
        ) {
            Surface(shadowElevation = 2.dp, color = Color.White) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filterOptions) { filter ->
                        FilterChip(
                            selected = uiState.selectedFilter == filter,
                            onClick = { vm.setFilter(filter) },
                            label = { Text(filter) },
                            leadingIcon = if (uiState.selectedFilter == filter) {
                                { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = primaryOrange.copy(alpha = 0.15f),
                                selectedLabelColor = primaryOrange,
                                selectedLeadingIconColor = primaryOrange
                            )
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryOrange)
                }
            } else if (filteredOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Tidak ada pesanan \"${uiState.selectedFilter}\"", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredOrders) { orderUiModel ->
                        OrderListItem(
                            model = orderUiModel,
                            primaryOrange = primaryOrange,
                            onClick = { backStack.add(Routes.OrderDetailRoute(orderUiModel.order.idOrder)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderListItem(model: OrderListUiModel, primaryOrange: Color, onClick: () -> Unit) {
    val lightOrange = Color(0xFFFFF3E0)
    val statusIndo = mapStatusToIndonesian(model.order.orderStatus)

    val statusColor = when (statusIndo) {
        "SELESAI" -> Color(0xFF4CAF50)
        "DIKIRIM" -> Color(0xFF2196F3)
        "DIPROSES" -> primaryOrange
        "MENUNGGU" -> Color(0xFFFF9800)
        "DIBATALKAN" -> Color(0xFFF44336)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Nama Toko & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = model.storeName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.DarkGray)
                }
                Text(text = statusIndo, style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.Bold)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color(0xFFF0F0F0))

            // Body: Nama Produk & Total Harga
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(52.dp), shape = RoundedCornerShape(12.dp), color = lightOrange) {
                    // Ikon sebagai pengganti gambar produk
                    Icon(Icons.Default.Storefront, null, modifier = Modifier.padding(12.dp), tint = primaryOrange)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {

                    // Menampilkan "+ x produk lain" jika lebih dari 1 barang
                    val productText = if (model.additionalItemCount > 0) {
                        "${model.firstItemName} (+${model.additionalItemCount} barang lainnya)"
                    } else {
                        model.firstItemName
                    }

                    Text(
                        text = productText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF212121),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Total: Rp ${model.order.grandTotal.toLong()}", color = Color.Gray, fontSize = 13.sp)
                }
                Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
            }
        }
    }
}

// Helper Mapping Status
fun mapStatusToIndonesian(status: String): String {
    return when (status) {
        "PENDING" -> "MENUNGGU"
        "PROCESSED" -> "DIPROSES"
        "SHIPPED" -> "DIKIRIM"
        "DELIVERED" -> "SELESAI"
        "CANCELLED" -> "DIBATALKAN"
        else -> status
    }
}