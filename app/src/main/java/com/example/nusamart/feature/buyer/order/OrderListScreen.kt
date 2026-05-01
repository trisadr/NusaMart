package com.example.nusamart.feature.buyer.order

import android.content.Context
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
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.unit.sp
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.entity.Order
import com.example.nusamart.entity.OrderStatus
import com.example.nusamart.ui.theme.NusaMartTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

fun loadAllOrders(context: Context): List<Order> {
    return try {
        val orderFile = File(context.filesDir, "order.json")
        val jsonString = if (orderFile.exists()) {
            orderFile.readText()
        } else {
            context.assets.open("order.json").bufferedReader().use { it.readText() }
        }
        val type = object : TypeToken<List<Order>>() {}.type
        Gson().fromJson(jsonString, type) ?: emptyList()
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

@Composable
fun OrderListScreen() {
    val context = LocalContext.current
    val backStack = LocalBackStack.current

    val orders = remember { loadAllOrders(context) }

    Content(
        orders = orders,
        onBackClick = {
            if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex)
        },
        onOrderClick = { orderId ->
            backStack.add(Routes.OrderDetailRoute(orderId))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    orders: List<Order>,
    onBackClick: () -> Unit,
    onOrderClick: (String) -> Unit
) {
    val primaryOrange = Color(0xFFFF6D00)

    val filterOptions = listOf("Semua") + OrderStatus.entries.map { it.name }
    var selectedFilter by remember { mutableStateOf("Semua") }

    val filteredOrders = remember(selectedFilter, orders) {
        if (selectedFilter == "Semua") orders
        else orders.filter { it.status.name == selectedFilter }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Pesanan Saya",
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryOrange
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
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
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            leadingIcon = if (selectedFilter == filter) {
                                {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
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

            if (filteredOrders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada pesanan \"$selectedFilter\"",
                        color = Color.Gray
                    )
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
                            onClick = { onOrderClick(order.idOrder) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderListItem(
    order: Order,
    primaryOrange: Color,
    onClick: () -> Unit
) {
    val lightOrange = Color(0xFFFFF3E0)

    val statusColor = when (order.status) {
        OrderStatus.SELESAI -> Color(0xFF4CAF50)
        OrderStatus.DIKIRIM -> Color(0xFF2196F3)
        OrderStatus.DIPROSES -> primaryOrange
        OrderStatus.MENUNGGU -> Color(0xFFFF9800)
        OrderStatus.DIBATALKAN -> Color(0xFFF44336)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(12.dp),
                color = lightOrange
            ) {
                Icon(
                    imageVector = Icons.Default.LocalMall,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = primaryOrange
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.status.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.idOrder,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF212121)
                )
                Text(
                    text = "Rp ${order.totalPrice.toLong()}",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OrderListScreenPreview() {
    val dummyOrders = listOf(
        Order("ORD001", 235000.0, OrderStatus.SELESAI, "JNE123", "Pesanan 1", 1713600000L, 1713900000L, "SELLER001"),
        Order("ORD002", 150000.0, OrderStatus.DIKIRIM, "SICEPAT987", "Pesanan 2", 1713700000L, null, "SELLER002"),
        Order("ORD003", 80000.0, OrderStatus.DIPROSES, "", "Pesanan 3", 1713800000L, null, "SELLER001"),
        Order("ORD004", 320000.0, OrderStatus.MENUNGGU, "", "Pesanan 4", 1713850000L, null, "SELLER003"),
        Order("ORD005", 55000.0, OrderStatus.DIBATALKAN, "", "Pesanan 5", 1713500000L, null, "SELLER002")
    )

    NusaMartTheme(dynamicColor = false) {
        Content(
            orders = dummyOrders,
            onBackClick = {},
            onOrderClick = {}
        )
    }
}
