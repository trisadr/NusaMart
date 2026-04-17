package com.example.nusamart.feature.screen

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpOutline
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nusamart.feature.components.NusaMartBottomNavigation
import com.example.nusamart.ui.theme.BlackText
import com.example.nusamart.ui.theme.GrayBackground
import com.example.nusamart.ui.theme.RedLight
import com.example.nusamart.ui.theme.RedPrimary
import com.example.nusamart.ui.theme.WhiteSurface

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
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
                    IconButton(onClick = { /* logic belum ada */ }) {
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
                selectedMenu = "Saya"
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WhiteSurface)
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // PP Placeholder
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

                    // info user
                    Column {
                        Text(
                            text = "Mahasigma", // data dummy
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlackText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "mahasigma@gmail.com", // data dummy
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigasi
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WhiteSurface)
            ) {
                ProfileMenuItem(
                    icon = Icons.Default.ShoppingCart,
                    title = "Pesanan Saya",
                    onClick = { /* logic belum ada */ }
                )
                HorizontalDivider(color = GrayBackground, thickness = 1.dp)

                ProfileMenuItem(
                    icon = Icons.Default.LocationOn,
                    title = "Alamat Pengiriman",
                    onClick = { /* logic belum ada */ }
                )
                HorizontalDivider(color = GrayBackground, thickness = 1.dp)

                ProfileMenuItem(
                    icon = Icons.Default.Payment,
                    title = "Metode Pembayaran",
                    onClick = { /* logic belum ada */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    icon = Icons.Default.HelpOutline,
                    title = "Pusat Bantuan",
                    onClick = { /* logic belum ada */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // LOgout
            OutlinedButton(
                onClick = { /* logic belum ada */ },
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
                    imageVector = Icons.Default.ExitToApp,
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

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
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
        Text(
            text = title,
            fontSize = 16.sp,
            color = BlackText,
            modifier = Modifier.weight(1f) // teks ambil sisa ruang kosong
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Detail",
            tint = Color.LightGray,
            modifier = Modifier.size(24.dp)
        )
    }
}