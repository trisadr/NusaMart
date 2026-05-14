package com.example.nusamart.feature.buyer.order.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.data.repository.order.OrderJson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(vm: OrderListVM = viewModel(factory = OrderListVM.Factory)) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    val primaryOrange = Color(0xFFFF6D00)
    val filterOptions = listOf("Semua", "MENUNGGU", "DIPROSES", "DIKIRIM", "SELESAI", "DIBATALKAN")

    // Filter data di UI layer berdasarkan state
    val filteredOrders = remember(uiState.selectedFilter, uiState.orders) {
        if (uiState.selectedFilter == "Semua") {
            uiState.orders
        } else {
            uiState.orders.filter { mapStatusToIndonesian(it.orderStatus) == uiState.selectedFilter }
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
                    items(filteredOrders) { order ->
                        OrderListItem(
                            order = order,
                            primaryOrange = primaryOrange,
                            onClick = { backStack.add(Routes.OrderDetailRoute(order.idOrder)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderListItem(order: OrderJson, primaryOrange: Color, onClick: () -> Unit) {
    val lightOrange = Color(0xFFFFF3E0)
    val statusIndo = mapStatusToIndonesian(order.orderStatus)

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
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(52.dp), shape = RoundedCornerShape(12.dp), color = lightOrange) {
                Icon(Icons.Default.LocalMall, null, modifier = Modifier.padding(12.dp), tint = primaryOrange)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = statusIndo, style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.Bold)
                Text(text = order.invoiceNumber, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF212121))
                Text(text = "Rp ${order.grandTotal.toLong()}", color = Color.Gray, fontSize = 13.sp)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
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