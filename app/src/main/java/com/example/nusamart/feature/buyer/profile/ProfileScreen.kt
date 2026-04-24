package com.example.nusamart.feature.buyer.profile

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.core.activeUser
import com.example.nusamart.entity.Buyer
import com.example.nusamart.feature.components.BottomMenu
import com.example.nusamart.feature.components.NusaMartBottomNavigation
import com.example.nusamart.ui.theme.BlackText
import com.example.nusamart.ui.theme.GrayBackground
import com.example.nusamart.ui.theme.NusaMartTheme
import com.example.nusamart.ui.theme.RedLight
import com.example.nusamart.ui.theme.RedPrimary
import com.example.nusamart.ui.theme.WhiteSurface
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Fungsi helper untuk membaca data JSON dari assets
fun loadBuyersFromJson(context: Context): List<Buyer> {
    return try {
        val jsonString = context.assets.open("buyer.json").bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<Buyer>>() {}.type
        Gson().fromJson(jsonString, listType)
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

// ==========================================
// 1. STATEFUL SCREEN (Mengatur Data & Navigasi)
// ==========================================
@Composable
fun ProfileScreen() {
    val backStack = LocalBackStack.current
    val context = LocalContext.current

    // State untuk menyimpan data user yang sedang login
    var currentBuyer by remember { mutableStateOf<Buyer?>(null) }

    // Memuat data buyer dari JSON saat halaman dibuka
    LaunchedEffect(Unit) {
        val buyers = loadBuyersFromJson(context)
        if (buyers.isNotEmpty()) {
            // Untuk simulasi, kita ambil data buyer pertama (index 0) sebagai user yang sedang login
            currentBuyer = buyers[0]
        }
    }

    ProfileContent(
        username = currentBuyer?.username ?: "Loading...",
        email = currentBuyer?.email ?: "Loading...",
        address = currentBuyer?.address ?: "Alamat belum diatur",
        onBackClick = {
            backStack.removeAt(backStack.lastIndex)
        },
        onPesananClick = {
            // Karena OrderListScreen butuh parameter, pastikan Routes kamu sudah disesuaikan
            // Atau cukup panggil rute OrderListRoute jika kamu belum mengubah Routes.kt
            backStack.add(Routes.OrderListRoute)
        },
        onLogoutClick = {
            activeUser = null
            // Menghapus semua tumpukan halaman agar tidak bisa di-back
            backStack.clear()
            // Pergi ke halaman Login
            backStack.add(Routes.LoginPageRoute)
        }
    )
}

// ==========================================
// 2. STATELESS CONTENT (Murni Tampilan UI)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    username: String,
    email: String,
    address: String,
    onBackClick: () -> Unit,
    onPesananClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profil Saya",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                // 1. Ganti "Saya" menjadi Enum BottomMenu.PROFILE
                selectedMenu = BottomMenu.PROFILE,

                // 2. Tambahkan aksi saat menu lain ditekan
                onMenuSelected = { menu ->
                    // Jangan lupa tambahkan val backStack = LocalBackStack.current di ProfileContent jika butuh langsung pindah,
                    // Atau panggil lewat parameter (lebih disarankan untuk Stateless)

                    /* Contoh navigasinya (sesuaikan dengan nama Route-mu):
                    when (menu) {
                        BottomMenu.HOME -> backStack.add(Routes.HomeRoute)
                        BottomMenu.NOTIFICATION -> backStack.add(Routes.NotificationRoute)
                        BottomMenu.CART -> backStack.add(Routes.CartRoute)
                        BottomMenu.PROFILE -> { /* Tidak melakukan apa-apa karena sudah di halaman ini */ }
                    }
                    */
                }
            )
        },
        containerColor = GrayBackground
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            // --- HEADER INFO USER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WhiteSurface)
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Placeholder Profil Picture
                    Surface(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape),
                        color = RedLight.copy(alpha = 0.2f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Foto Profil",
                            tint = RedPrimary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Info Data User Dinamis
                    Column {
                        Text(
                            text = username,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlackText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = email,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- MENU NAVIGASI 1 ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WhiteSurface)
            ) {
                ProfileMenuItem(
                    icon = Icons.Default.ShoppingCart,
                    title = "Pesanan Saya",
                    onClick = onPesananClick
                )
                HorizontalDivider(color = GrayBackground, thickness = 1.dp)

                // Menu Alamat - Ditambahkan parameter 'subtitle'
                ProfileMenuItem(
                    icon = Icons.Default.LocationOn,
                    title = "Alamat Pengiriman",
                    subtitle = address, // Akan menampilkan teks alamat di bawah judul
                    onClick = { /* Belum bisa dipencet sesuai permintaan */ }
                )
                HorizontalDivider(color = GrayBackground, thickness = 1.dp)

                ProfileMenuItem(
                    icon = Icons.Default.Payment,
                    title = "Metode Pembayaran",
                    onClick = { /* logic belum ada */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- MENU NAVIGASI 2 ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WhiteSurface)
            ) {
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Pengaturan Akun",
                    onClick = { /* logic belum ada */ }
                )
                HorizontalDivider(color = GrayBackground, thickness = 1.dp)

                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    title = "Pusat Bantuan",
                    onClick = { /* logic belum ada */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- TOMBOL LOGOUT ---
            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, RedPrimary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = RedPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Log Out",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ==========================================
// REUSABLE MENU ITEM COMPONENT
// ==========================================
@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null, // Tambahan opsional untuk teks di bawah judul
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        // Kita ubah menjadi Column agar bisa menampung Title dan Subtitle (Alamat)
        Column(
            modifier = Modifier.weight(1f) // Ambil sisa ruang kosong
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = BlackText
            )
            // Jika ada subtitle (seperti alamat pengiriman), tampilkan di sini
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Detail",
            tint = Color.LightGray,
            modifier = Modifier.size(24.dp)
        )
    }
}

// ==========================================
// 3. PREVIEW
// ==========================================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    NusaMartTheme {
        ProfileContent(
            username = "buyer_01",
            email = "buyer.satu@gmail.com",
            address = "Jl. Ir. Sutami No.36, Kentingan, Surakarta",
            onBackClick = {},
            onPesananClick = {},
            onLogoutClick = {}
        )
    }
}