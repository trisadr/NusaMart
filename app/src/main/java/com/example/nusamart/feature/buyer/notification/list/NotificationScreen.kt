package com.example.nusamart.feature.buyer.notification.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.data.repository.notif.NotificationJson
import com.example.nusamart.feature.components.BottomMenu
import com.example.nusamart.feature.components.NusaMartBottomNavigation
import kotlin.collections.filter

// --- SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    vm: NotificationListVM = viewModel(factory = NotificationListVM.Factory)
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    // Load setiap kali screen dibuka
    LaunchedEffect(Unit) { vm.loadNotifications() }

    val systemNotifs = uiState.notifications.filter { it.type == "SISTEM" }
    val orderNotifs = uiState.notifications.filter { it.type == "ORDER" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifikasi", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NusaMartBottomNavigation(
                selectedMenu = BottomMenu.NOTIFICATION,
                onMenuSelected = { menu ->
                    when (menu) {
                        BottomMenu.HOME -> backStack.add(Routes.HomeRoute)
                        BottomMenu.NOTIFICATION -> Unit
                        BottomMenu.PROFILE -> backStack.add(Routes.ProfileRoute)
                        BottomMenu.CART -> backStack.add(Routes.CartRoute)
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (uiState.notifications.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) { Text("Belum ada notifikasi", color = Color.Gray) }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

                // KELOMPOK 1: SISTEM / PROMO
                if (systemNotifs.isNotEmpty()) {
                    itemsIndexed(systemNotifs) { _, notif ->
                        NotificationItem(
                            notif = notif,
                            icon = Icons.Default.Campaign,
                            iconColor = Color(0xFF2196F3),
                            onClick = { backStack.add(Routes.NotificationDetailRoute(notif.idNotif)) }
                        )
                    }
                }

                // KELOMPOK 2: PESANAN
                if (orderNotifs.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Status Pesanan", fontSize = 14.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
                            Text("Tandai Semua Dibaca", fontSize = 12.sp, color = Color(0xFFFF6D00), modifier = Modifier.clickable { vm.markAllAsRead() })
                        }
                    }
                    itemsIndexed(orderNotifs) { _, notif ->
                        NotificationItem(
                            notif = notif,
                            icon = Icons.Default.Inventory,
                            iconColor = Color(0xFFFF9800),
                            onClick = { backStack.add(Routes.NotificationDetailRoute(notif.idNotif)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notif: NotificationJson,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    val bgColor = if (notif.isRead) Color.White else Color(0xFFFFF8E1) // Warna beda jika belum dibaca

    Column(modifier = Modifier.fillMaxWidth().background(bgColor).clickable(onClick = onClick)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Surface(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(8.dp), color = iconColor) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.padding(10.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(notif.title, fontSize = 15.sp, fontWeight = if (notif.isRead) FontWeight.Normal else FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(notif.body, fontSize = 13.sp, color = Color.DarkGray, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(8.dp))
                Text(notif.createAt, fontSize = 11.sp, color = Color.Gray)
            }
            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.LightGray, modifier = Modifier.align(Alignment.CenterVertically))
        }
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
    }
}