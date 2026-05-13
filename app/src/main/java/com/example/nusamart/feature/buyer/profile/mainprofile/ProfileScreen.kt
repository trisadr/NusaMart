package com.example.nusamart.feature.buyer.profile.mainprofile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.feature.components.BottomMenu
import com.example.nusamart.feature.components.NusaMartBottomNavigation
import com.example.nusamart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(vm: ProfileVM = viewModel(factory = ProfileVM.Factory)) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Saya", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BlackText) },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = BlackText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WhiteSurface)
            )
        },
        bottomBar = {
            NusaMartBottomNavigation(
                selectedMenu = BottomMenu.PROFILE,
                onMenuSelected = { menu ->
                    when (menu) {
                        BottomMenu.HOME -> backStack.add(Routes.HomeRoute)
                        BottomMenu.NOTIFICATION -> backStack.add(Routes.NotificationRoute)
                        BottomMenu.CART -> backStack.add(Routes.CartRoute)
                        BottomMenu.PROFILE -> Unit
                    }
                }
            )
        },
        containerColor = GrayBackground
    ) { paddingValues ->

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val user = uiState.user

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // --- HEADER PROFIL ---
                Box(
                    modifier = Modifier.fillMaxWidth().background(WhiteSurface).padding(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(72.dp).clip(CircleShape),
                            color = RedLight.copy(alpha = 0.2f)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = RedPrimary, modifier = Modifier.padding(16.dp))
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(text = user?.username ?: "Pengguna", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BlackText)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = user?.email ?: "-", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- MENU PESANAN & ALAMAT ---
                Column(modifier = Modifier.fillMaxWidth().background(WhiteSurface)) {
                    ProfileMenuItem(
                        icon = Icons.Default.ShoppingCart,
                        title = "Pesanan Saya",
                        onClick = { backStack.add(Routes.OrderListRoute) }
                    )
                    HorizontalDivider(color = GrayBackground, thickness = 1.dp)

                    ProfileMenuItem(
                        icon = Icons.Default.LocationOn,
                        title = "Alamat Pengiriman",
                        subtitle = "Atur alamat pengirimanmu",
                        onClick = { backStack.add(Routes.AddressListRoute) } // Arahkan ke Layar Alamat
                    )
                    // Menu Metode Pembayaran sudah dihapus
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- MENU PENGATURAN ---
                Column(modifier = Modifier.fillMaxWidth().background(WhiteSurface)) {
                    ProfileMenuItem(icon = Icons.Default.Settings, title = "Pengaturan Akun", onClick = {})
                    HorizontalDivider(color = GrayBackground, thickness = 1.dp)
                    ProfileMenuItem(icon = Icons.AutoMirrored.Filled.HelpOutline, title = "Pusat Bantuan", onClick = {})
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- TOMBOL LOGOUT ---
                OutlinedButton(
                    onClick = {
                        vm.logout()
                        backStack.clear()
                        backStack.add(Routes.LoginPageRoute)
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, RedPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RedPrimary)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, title: String, subtitle: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = Color.Gray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, color = BlackText)
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 13.sp, color = Color.Gray, lineHeight = 16.sp)
            }
        }
        Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.LightGray, modifier = Modifier.size(24.dp))
    }
}