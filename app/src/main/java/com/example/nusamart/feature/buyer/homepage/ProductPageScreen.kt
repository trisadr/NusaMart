//package com.example.nusamart.feature.screen
//
//import android.content.Intent
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.MailOutline
//import androidx.compose.material.icons.filled.Share
//import androidx.compose.material.icons.filled.ShoppingCart
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextDecoration
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.example.nusamart.feature.entity.Product
//import com.example.nusamart.ui.theme.NusaMartTheme
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProductPageScreen(
//    product: Product,
//    onBackClick: () -> Unit
//) {
//    val context = LocalContext.current
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Detail Produk") },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = {
//                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
//                            type = "text/plain"
//                            putExtra(Intent.EXTRA_SUBJECT, "Cek produk ini!")
//                            putExtra(Intent.EXTRA_TEXT, "Hei, lihat ${product.name} ini! Cuma Rp${product.price.toInt()} di NusaMart.\n\n${product.description}")
//                        }
//                        context.startActivity(Intent.createChooser(shareIntent, "Bagikan"))
//                    }) {
//                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
//                    }
//                }
//            )
//        },
//        bottomBar = {
//            BottomAppBar(
//                containerColor = MaterialTheme.colorScheme.surfaceVariant,
//                contentPadding = PaddingValues(horizontal = 16.dp)
//            ) {
//                IconButton(onClick = { }) {
//                    Icon(imageVector = Icons.Default.MailOutline, contentDescription = "Chat Penjual")
//                }
//                Spacer(modifier = Modifier.width(8.dp))
//                IconButton(onClick = { }) {
//                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Tambah ke Keranjang")
//                }
//                Spacer(modifier = Modifier.weight(1f))
//                Button(
//                    onClick = { },
//                    modifier = Modifier.fillMaxWidth(0.8f)
//                ) {
//                    Text("Beli Sekarang")
//                }
//            }
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .padding(innerPadding)
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//        ) {
//            Image(
//                painter = painterResource(id = product.imageRes),
//                contentDescription = product.name,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .aspectRatio(1f)
//                    .background(Color.LightGray),
//                contentScale = ContentScale.Crop
//            )
//
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text(
//                    text = "Rp ${product.price.toInt()}",
//                    style = MaterialTheme.typography.headlineMedium,
//                    color = MaterialTheme.colorScheme.primary,
//                    fontWeight = FontWeight.Bold
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Text(
//                    text = product.name,
//                    style = MaterialTheme.typography.titleLarge,
//                    fontWeight = FontWeight.Medium
//                )
//
//                Spacer(modifier = Modifier.height(4.dp))
//
//                Text(
//                    text = "Dikirim dari: ${product.location}",
//                    style = MaterialTheme.typography.labelLarge,
//                    color = MaterialTheme.colorScheme.primary,
//                    textDecoration = TextDecoration.Underline,
//                    modifier = Modifier
//                        .clickable {
//                            try {
//                                val mapIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(product.map))
//                                context.startActivity(mapIntent)
//                            } catch (e: Exception) {
//                                android.widget.Toast.makeText(
//                                    context,
//                                    "Link lokasi tidak ditemukan",
//                                    android.widget.Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                        .padding(vertical = 2.dp)
//                )
//
//                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
//
//                Text(
//                    text = "Deskripsi Produk",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Text(
//                    text = product.description,
//                    style = MaterialTheme.typography.bodyLarge
//                )
//            }
//        }
//    }
//}
//
//// Ini contoh untuk mengecek preview
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun ProductPagePreview() {
//    val contohProduk = Product(
//        id = 99,
//        name = "Sepatu Contoh Preview",
//        price = 150000f,
//        location = "Kota Jakarta",
//        imageRes = android.R.drawable.ic_menu_gallery,
//        description = "Ini adalah teks deskripsi",
//        map = "https://maps.app.goo.gl/TaYArGwxrpCBy1EN9"
//    )
//
//    NusaMartTheme(dynamicColor = false) {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
//        ) {
//            ProductPageScreen(
//                product = contohProduk,
//                onBackClick = { }
//            )
//        }
//    }
//}