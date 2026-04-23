//package com.example.nusamart.feature.screen
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.LocalShipping
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.nusamart.model.dummyOrderList
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun OrderDetailScreen(orderId: String?) {
//    val order = dummyOrderList.find { it.id == orderId } ?: return
//    val primaryOrange = Color(0xFFFF6D00)
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Rincian Pesanan", fontWeight = FontWeight.Bold) },
//                navigationIcon = {
//                    IconButton(onClick = { /* logic belum ada */ }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = null)
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//                .background(Color.White)
//                .padding(20.dp)
//        ) {
//            Text(text = "Status Pesanan", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
//            Text(
//                text = order.status.label,
//                color = primaryOrange,
//                fontSize = 24.sp,
//                fontWeight = FontWeight.ExtraBold
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(16.dp),
//                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
//            ) {
//                Row(modifier = Modifier.padding(16.dp)) {
//                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = primaryOrange)
//                    Spacer(modifier = Modifier.width(12.dp))
//                    Column {
//                        Text("Informasi Pengiriman", fontWeight = FontWeight.Bold)
//                        Text("No. Resi: ${order.trackingNumber}", fontSize = 13.sp)
//                        Text("Alamat: ${order.address}", fontSize = 13.sp, color = Color.DarkGray)
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Text("Detail Produk", fontWeight = FontWeight.Bold, fontSize = 18.sp)
//            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
//
//            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//                Column(modifier = Modifier.weight(1f)) {
//                    Text(order.productName, fontWeight = FontWeight.SemiBold)
//                    Text(order.description, fontSize = 12.sp, color = Color.Gray)
//                }
//                Text("Rp ${order.totalPrice.toInt()}", fontWeight = FontWeight.Bold)
//            }
//
//            Spacer(modifier = Modifier.height(40.dp))
//
//            Button(
//                onClick = { /* Chat Penjual */ },
//                modifier = Modifier.fillMaxWidth().height(50.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = primaryOrange)
//            ) {
//                Text("Tanyakan Penjual", fontWeight = FontWeight.Bold, color = Color.White)
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//private fun OrderDetailScreenPreview() {
//    OrderDetailScreen(orderId = "ORD-001") //contoh data dummy
//}