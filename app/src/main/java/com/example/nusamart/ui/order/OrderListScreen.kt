package com.example.nusamart.ui.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nusamart.model.OrderStatus
import com.example.nusamart.model.dummyOrderList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(navController: NavController) {
    val primaryOrange = Color(0xFFFF6D00)
    val lightOrange = Color(0xFFFFF3E0)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pesanan Saya", fontWeight = FontWeight.ExtraBold, color = primaryOrange) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFFAFAFA)),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(dummyOrderList) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { navController.navigate("detail/${order.id}") },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = lightOrange
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalMall,
                                contentDescription = null,
                                modifier = Modifier.padding(14.dp),
                                tint = primaryOrange
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = order.status.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = primaryOrange,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = order.productName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF212121)
                            )
                            Text(
                                text = "Rp ${order.totalPrice.toInt()}",
                                color = Color.Gray,
                                fontSize = 14.sp
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
        }
    }
}