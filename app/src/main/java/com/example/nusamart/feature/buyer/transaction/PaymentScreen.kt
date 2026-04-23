//package com.example.nusamart.feature.screen
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
//import androidx.compose.material.icons.filled.AccountBalanceWallet
//import androidx.compose.material.icons.filled.LocalShipping
//import androidx.compose.material.icons.filled.LocationOn
//import androidx.compose.material3.Button
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedCard
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.example.nusamart.feature.entity.Product
//import com.example.nusamart.feature.entity.dummyProductList
//import com.example.nusamart.ui.theme.NusaMartTheme
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PaymentScreen(
//    product: Product
//) {
//    // State untuk menyimpan teks catatan dari pembeli
//    var noteText by remember { mutableStateOf("") }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Checkout") },
//                navigationIcon = {
//                    IconButton(onClick = { }) {
//                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
//                    }
//                }
//            )
//        },
//        bottomBar = {
//            Surface(
//                shadowElevation = 8.dp,
//                tonalElevation = 2.dp
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth().padding(16.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Column {
//                        Text(text = "Total Tagihan", style = MaterialTheme.typography.labelLarge)
//                        Text(
//                            text = "Rp ${product.price.toInt() + 15000}", // misal sudah include ongkir
//                            style = MaterialTheme.typography.titleLarge,
//                            color = MaterialTheme.colorScheme.primary,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                    Button(
//                        onClick = {},
//                        modifier = Modifier.width(180.dp)
//                    ) {
//                        Text("Buat Pesanan")
//                    }
//                }
//            }
//        }
//    ) { innerPadding ->
//        LazyColumn(
//            modifier = Modifier.fillMaxSize().padding(innerPadding).background(MaterialTheme.colorScheme.surface),
//            contentPadding = PaddingValues(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//
//            item {
//                Text(text = "Alamat Pengiriman", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
//                Card(
//                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
//                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
//                ) {
//                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
//                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
//                        Spacer(modifier = Modifier.width(12.dp))
//                        Column {
//                            // alamat misal
//                            Text(text = "Budi Santoso | (+62) 812-3456-789", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
//                            Text(text = "Jl. Merdeka No. 123, Kel. Suka Maju, Kec. Pintar, Kota Surakarta, Jawa Tengah 57123", style = MaterialTheme.typography.bodySmall)
//                        }
//                    }
//                }
//            }
//
//            item {
//                HorizontalDivider()
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(text = "Pesanan Kamu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
//                Row(
//                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Image(
//                        painter = painterResource(id = product.imageRes),
//                        contentDescription = null,
//                        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
//                        contentScale = ContentScale.Crop
//                    )
//                    Spacer(modifier = Modifier.width(16.dp))
//                    Column(modifier = Modifier.weight(1f)) {
//                        Text(text = product.name, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
//                        Text(text = "1 Barang", style = MaterialTheme.typography.bodySmall)
//                        Text(
//                            text = "Rp ${product.price.toInt()}",
//                            style = MaterialTheme.typography.bodyMedium,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
//
//                // --- BAGIAN CATATAN BARU DITAMBAHKAN ---
//                Spacer(modifier = Modifier.height(4.dp))
//                OutlinedTextField(
//                    value = noteText,
//                    onValueChange = { noteText = it },
//                    label = { Text("Pesan untuk penjual (Opsional)") },
//                    modifier = Modifier.fillMaxWidth(),
//                    singleLine = true,
//                    shape = RoundedCornerShape(12.dp)
//                )
//            }
//
//            // --- BAGIAN OPSI PENGIRIMAN BARU DITAMBAHKAN ---
//            item {
//                HorizontalDivider()
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(text = "Opsi Pengiriman", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
//                OutlinedCard(
//                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
//                    onClick = { /* Pilih Pengiriman */ }
//                ) {
//                    Row(
//                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
//                            Spacer(modifier = Modifier.width(12.dp))
//                            Column {
//                                Text(text = "Reguler (Rp 12.000)", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
//                                Text(text = "Estimasi diterima: 19 - 21 Apr", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
//                            }
//                        }
//                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
//                    }
//                }
//            }
//
//            item {
//                HorizontalDivider()
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(text = "Metode Pembayaran", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
//                OutlinedCard(
//                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
//                    onClick = { /* Pilih Metode */ }
//                ) {
//                    Row(
//                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
//                            Spacer(modifier = Modifier.width(12.dp))
//                            Text(text = "Transfer Bank (BCA)", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
//                        }
//                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
//                    }
//                }
//            }
//
//            item {
//                HorizontalDivider()
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(text = "Rincian Pembayaran", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
//
//                DetailRow(label = "Subtotal untuk Produk", value = "Rp ${product.price.toInt()}")
//                DetailRow(label = "Subtotal Pengiriman", value = "Rp 12.000")
//                DetailRow(label = "Biaya Layanan", value = "Rp 3.000")
//
//                Spacer(modifier = Modifier.height(8.dp))
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text(text = "Total Pembayaran", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
//                    Text(
//                        text = "Rp ${product.price.toInt() + 15000}",
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//            }
//
//        }
//    }
//}
//
//@Composable
//fun DetailRow(label: String, value: String) {
//    Row(
//        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
//        Text(text = value, style = MaterialTheme.typography.bodyMedium)
//    }
//}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PaymentScreenPreview() {
//    val dummyProduct = dummyProductList[0]
//
//    NusaMartTheme(dynamicColor = false) {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
//        ) {
//            PaymentScreen(
//                product = dummyProduct
//            )
//        }
//    }
//}