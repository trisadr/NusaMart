package com.example.nusamart.feature.buyer.homepage.product

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nusamart.R
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPageScreen(
    productId: String,
    vm: ProductPageVM = viewModel(factory = ProductPageVM.Factory)
) {
    val context = LocalContext.current
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val redColor = Color(0xFFF05555)

    LaunchedEffect(productId) {
        vm.loadProduct(productId)
    }

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = redColor) }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Produk", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }) { Icon(Icons.Default.ArrowBack, "Kembali") } },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Lihat produk ini di NusaMart: ${uiState.productName} mulai dari ${formatPrice(uiState.minPrice)}!")
                        }
                        context.startActivity(Intent.createChooser(intent, "Bagikan produk"))
                    }) { Icon(Icons.Default.Share, "Bagikan") }
                }
            )
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier.height(72.dp), containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { Toast.makeText(context, "Fitur chat segera hadir", Toast.LENGTH_SHORT).show() }) {
                        Icon(Icons.Default.MailOutline, "Chat Penjual", tint = Color.DarkGray)
                    }
                    IconButton(onClick = { vm.openSheet(SheetMode.CART) }) {
                        Icon(Icons.Default.ShoppingCart, "Tambah ke Keranjang", tint = Color.DarkGray)
                    }
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = { vm.openSheet(SheetMode.BUY) },
                        modifier = Modifier.padding(end = 8.dp).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = redColor)
                    ) { Text("Beli Sekarang", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())) {

            // --- Image Pager ---
            val pagerState = rememberPagerState(pageCount = { uiState.images.size })
            Box {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().aspectRatio(1f).background(Color.LightGray)) { page ->
                    Image(
                        painter = painterResource(uiState.images[page]),
                        contentDescription = uiState.productName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                // Pager Indicator
                if (uiState.images.size > 1) {
                    Surface(
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                        color = Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("${pagerState.currentPage + 1}/${uiState.images.size}", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp)
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // --- Price ---
                val priceText = if (uiState.minPrice == uiState.maxPrice) formatPrice(uiState.minPrice)
                else "${formatPrice(uiState.minPrice)} - ${formatPrice(uiState.maxPrice)}"

                Text(text = priceText, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = redColor)
                Spacer(Modifier.height(8.dp))

                Text(text = uiState.productName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))

                Text(text = "Total Stok: ${uiState.totalStock}", fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Dikirim dari: ${uiState.storeLocation}",
                    color = redColor,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable {

                        val url = uiState.storeUrlLocation

                        if (!url.isNullOrBlank()) {

                            try {

                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(url)
                                )

                                context.startActivity(intent)

                            } catch (e: Exception) {

                                Toast.makeText(
                                    context,
                                    "Gagal membuka Google Maps",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {

                            Toast.makeText(
                                context,
                                "Lokasi toko belum tersedia",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray)

                Text("Deskripsi Produk", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                Text(text = uiState.productDescription, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray)

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Image(painter = painterResource(R.drawable.nm_logo), contentDescription = "Foto Toko", contentScale = ContentScale.Crop, modifier = Modifier.size(44.dp).clip(CircleShape))
                    Spacer(Modifier.width(12.dp))
                    Text(text = uiState.storeName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // --- Bottom Sheet (Variations & Quantity) ---
    if (uiState.sheetMode != SheetMode.NONE) {
        val selectedItem = uiState.items.find { it.idItem == uiState.selectedItemId }
        val priceToShow = selectedItem?.price ?: uiState.minPrice
        val stockToShow = selectedItem?.stock ?: 0

        ModalBottomSheet(
            onDismissRequest = vm::closeSheet, sheetState = sheetState, containerColor = Color.White
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 32.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(uiState.images.firstOrNull() ?: R.drawable.nm_logo), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(formatPrice(priceToShow), color = redColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Stok: $stockToShow", color = Color.Gray, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Variasi (Jika lebih dari 1 item)
                if (uiState.items.size > 1) {
                    Text("Pilih Variasi", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(uiState.items) { item ->
                            val isSelected = item.idItem == uiState.selectedItemId
                            Surface(
                                onClick = { vm.selectItem(item.idItem) },
                                shape = RoundedCornerShape(8.dp),
                                border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray),
                                color = if (isSelected) redColor.copy(alpha = 0.1f) else Color.Transparent
                            ) {
                                Text(item.variationName, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = if (isSelected) redColor else Color.Black, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Jumlah", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    OutlinedIconButton(onClick = vm::decreaseQuantity, enabled = uiState.quantity > 1, modifier = Modifier.size(36.dp)) { Text("−", fontWeight = FontWeight.Bold) }
                    Text(uiState.quantity.toString(), modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    OutlinedIconButton(onClick = vm::increaseQuantity, enabled = uiState.quantity < stockToShow, modifier = Modifier.size(36.dp)) { Text("+", fontWeight = FontWeight.Bold) }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (uiState.sheetMode == SheetMode.CART) {
                            vm.addToCart { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            vm.closeSheet()
                            // Melempar productId ke checkout route
                            backStack.add(
                                Routes.CheckoutRoute(
                                    productId = uiState.productId,
                                    quantity = uiState.quantity,
                                    fromCart = false
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = stockToShow > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = redColor)
                ) {
                    Text(
                        text = if (uiState.sheetMode == SheetMode.CART) "Masukkan ke Keranjang" else "Beli Sekarang",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// Helper
fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp ${formatter.format(price.toLong())}"
}