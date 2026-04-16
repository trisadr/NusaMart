package com.example.nusamart.ui.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nusamart.model.dummyOrderList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(navController: NavController, orderId: String?) {
    val order = dummyOrderList.find { it.id == orderId } ?: return
    val primaryOrange = Color(0xFFFF6D00)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rincian Pesanan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(20.dp)
        ) {
            Text(text = "Status Pesanan", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
            Text(
                text = order.status.label,
                color = primaryOrange,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = primaryOrange)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Informasi Pengiriman", fontWeight = FontWeight.Bold)
                        Text("No. Resi: ${order.trackingNumber}", fontSize = 13.sp)
                        Text("Alamat: ${order.address}", fontSize = 13.sp, color = Color.DarkGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Detail Produk", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(order.productName, fontWeight = FontWeight.SemiBold)
                    Text(order.description, fontSize = 12.sp, color = Color.Gray)
                }
                Text("Rp ${order.totalPrice.toInt()}", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))


            Button(
                onClick = { /* Chat Penjual */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryOrange)
            ) {
                Text("Tanyakan Penjual", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}