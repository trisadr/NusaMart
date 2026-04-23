package com.example.nusamart.feature.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nusamart.feature.components.NusaMartBottomNavigation
import com.example.nusamart.ui.theme.BlackText
import com.example.nusamart.ui.theme.BluePrimary
import com.example.nusamart.ui.theme.GrayBackground
import com.example.nusamart.ui.theme.OrangePrimary
import com.example.nusamart.ui.theme.WhiteSurface

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToProduct: (String, String, String) -> Unit,
    onNavigateToOrder: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifikasi",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = BlackText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteSurface
                )
            )
        },
        bottomBar = {
            NusaMartBottomNavigation(
                selectedMenu = "Notifikasi"
            )
        },
        containerColor = GrayBackground
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Bagian atas
            item {
                Column(modifier = Modifier.background(WhiteSurface)) {

                    PromoNotificationItem(
                        icon = Icons.Default.ShoppingCart,
                        iconBackgroundColor = BluePrimary,
                        title = "Keranjang Belanjamu Menunggu!",
                        message = "Hai Mahasigma, produk anyaman bambu yang kamu masukkan ke keranjang belum dicheckout nih. Yuk segera selesaikan sebelum stoknya habis!",
                        date = "17-04-2026 09:00",
                        onClick = onNavigateToCart
                    )

                    PromoNotificationItem(
                        icon = Icons.Default.Storefront,
                        iconBackgroundColor = OrangePrimary,
                        title = "Produk Ini Lagi Laris, Lho!",
                        message = "Temukan produk kerajinan terlaris buat hiasan rumahmu di sini \uD83D\uDC49",
                        date = "16-04-2026 18:02",
                        showImageGallery = true,
                        onClick = {
                            onNavigateToProduct(
                                "PROD-001",
                                "Produk Ini Lagi Laris, Lho!",
                                "Temukan produk kerajinan terlaris buat hiasan rumahmu di sini 👉"
                            )
                        }
                    )
                }
            }

            // Status pesanan
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GrayBackground)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Status Pesanan",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Tandai Sudah Dibaca",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.clickable { /* Logic tandai baca belum ada */ }
                    )
                }
            }

            // Bagian bawah
            item {
                Column(modifier = Modifier.background(WhiteSurface)) {
                    OrderNotificationItem(
                        icon = Icons.Default.Inventory,
                        title = "Pesanan Tiba di Tujuan",
                        message = "Nilai pesanan dan dapatkan koin tambahan untuk belanjamu selanjutnya.",
                        date = "17-04-2026 14:01",
                        onClick = {onNavigateToOrder("ORDER-002")}
                    )
                    HorizontalDivider(color = GrayBackground, thickness = 1.dp, modifier = Modifier.padding(start = 80.dp))

                    OrderNotificationItem(
                        icon = Icons.Default.LocalShipping,
                        title = "Pesanan NM260329... telah tiba",
                        message = "Mohon konfirmasi terima pesanan dalam 2x24 jam sebelum dana ditransfer ke Penjual.",
                        date = "16-04-2026 14:01",
                        onClick = {onNavigateToOrder("ORDER-001")}
                    )
                }
            }
        }
    }
}

@Composable
fun PromoNotificationItem(
    icon: ImageVector,
    iconBackgroundColor: Color,
    title: String,
    message: String,
    date: String,
    showImageGallery: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon Kotak
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(4.dp),
                color = iconBackgroundColor
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = WhiteSurface,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Kolom teks
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    color = BlackText,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    maxLines = 1, // Buat teksnya kepotong (separo)
                    overflow = TextOverflow.Ellipsis // Tambah titik-titik "..." di ujung teks
                )

                // Kotak gambar produk
                if (showImageGallery) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(3) {
                            Surface(
                                modifier = Modifier.size(60.dp),
                                shape = RoundedCornerShape(4.dp),
                                color = GrayBackground
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Gambar Produk",
                                    tint = Color.Gray,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = date,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Icon (>)
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Lihat Detail",
                tint = Color.LightGray,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically)
            )
        }

        HorizontalDivider(color = GrayBackground, thickness = 1.dp)
    }
}

// Notifikasi status pesanan
@Composable
fun OrderNotificationItem(
    icon: ImageVector,
    title: String,
    message: String,
    date: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(4.dp),
            color = GrayBackground
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Gambar Produk",
                tint = Color.Gray,
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = BlackText,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                fontSize = 13.sp,
                color = Color.DarkGray,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = date,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Detail Pesanan",
            tint = Color.LightGray,
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.CenterVertically)
        )
    }
}