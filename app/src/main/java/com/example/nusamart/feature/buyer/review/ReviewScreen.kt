//package com.example.nusamart.feature.screen
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.AddAPhoto
//import androidx.compose.material.icons.filled.Star
//import androidx.compose.material3.Button
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
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
//fun ReviewScreen(
//    product: Product
//) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Tulis Ulasan") },
//                navigationIcon = {
//                    IconButton(onClick = { }) {
//                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
//                    }
//                }
//            )
//        },
//        bottomBar = {
//            Surface(shadowElevation = 8.dp) {
//                Button(
//                    onClick = { },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                ) {
//                    Text("Kirim Ulasan")
//                }
//            }
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .padding(16.dp)
//                .verticalScroll(rememberScrollState()),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // --- INFO PRODUK SINGKAT ---
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Image(
//                    painter = painterResource(id = product.imageRes),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(60.dp)
//                        .clip(RoundedCornerShape(8.dp)),
//                    contentScale = ContentScale.Crop
//                )
//                Spacer(modifier = Modifier.width(16.dp))
//                Text(
//                    text = product.name,
//                    style = MaterialTheme.typography.titleMedium,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis
//                )
//            }
//
//            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))
//
//            // --- RATING BINTANG (1-5) ---
//            Text(
//                text = "Bagaimana kualitas produk ini?",
//                style = MaterialTheme.typography.bodyLarge,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Row {
//                repeat(5) { index ->
//                    Icon(
//                        imageVector = Icons.Default.Star,
//                        contentDescription = null,
//                        tint = if (index < 0) Color(0xFFFFC107) else Color.LightGray,
//                        modifier = Modifier.size(40.dp).padding(4.dp)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // --- PLACEHOLDER UPLOAD FOTO ---
//            Text(
//                text = "Tambahkan Foto Produk",
//                style = MaterialTheme.typography.bodyLarge,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.align(Alignment.Start)
//            )
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Box(
//                modifier = Modifier
//                    .size(100.dp)
//                    .clip(RoundedCornerShape(12.dp))
//                    .background(MaterialTheme.colorScheme.surfaceVariant)
//                    .clickable { /* Simulasi buka galeri */ }
//                    .align(Alignment.Start),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Icon(
//                        imageVector = Icons.Default.AddAPhoto,
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                    Text(
//                        text = "Tambah",
//                        style = MaterialTheme.typography.labelSmall,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // --- INPUT TEKS ULASAN ---
//            Text(
//                text = "Tulis ulasanmu",
//                style = MaterialTheme.typography.bodyLarge,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.align(Alignment.Start)
//            )
//            Spacer(modifier = Modifier.height(12.dp))
//            OutlinedTextField(
//                value = "",
//                onValueChange = { },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(150.dp),
//                placeholder = {
//                    Text("Ceritakan pengalamanmu menggunakan produk ini agar pembeli lain terbantu...")
//                },
//                shape = RoundedCornerShape(12.dp)
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun ReviewPreview() {
//    val dummyProduct = dummyProductList[0]
//
//    NusaMartTheme(dynamicColor = false) {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
//        ) {
//            ReviewScreen(
//                product = dummyProduct
//            )
//        }
//    }
//}