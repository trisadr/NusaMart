package com.example.nusamart.feature.buyer.notification.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.data.repository.notif.NotificationJson
import com.example.nusamart.feature.components.BottomMenu
import com.example.nusamart.feature.components.NusaMartBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    vm: NotificationListVM = hiltViewModel()
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) { vm.loadNotifications() }

    val systemNotifs = uiState.notifications.filter { it.type == "SISTEM" }
    val orderNotifs = uiState.notifications.filter { it.type == "ORDER" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifikasi",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) }) {
                        Icon(Icons.Default.ArrowBack, "Kembali", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (uiState.notifications.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Belum ada notifikasi", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

                // SISTEM / PROMO
                if (systemNotifs.isNotEmpty()) {
                    itemsIndexed(systemNotifs) { _, notif ->
                        NotificationItem(
                            notif = notif,
                            icon = Icons.Default.Campaign,
                            containerIconColor = Color(0xFF4DB6AC), // Mint/Teal Terang kustom
                            iconTint = Color.White,
                            onClick = { backStack.add(Routes.NotificationDetailRoute(notif.idNotif)) }
                        )
                    }
                }

                // PESANAN
                if (orderNotifs.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Status Pesanan",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Tandai Semua Dibaca",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { vm.markAllAsRead() }
                            )
                        }
                    }
                    itemsIndexed(orderNotifs) { _, notif ->
                        NotificationItem(
                            notif = notif,
                            icon = Icons.Default.Inventory,
                            containerIconColor = Color(0xFF00736B), // Deep Teal kustom untuk pesanan
                            iconTint = Color.White,
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
    containerIconColor: androidx.compose.ui.graphics.Color,
    iconTint: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    val bgColor = if (notif.isRead) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
    }

    Column(modifier = Modifier.fillMaxWidth().background(bgColor).clickable(onClick = onClick)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                color = containerIconColor
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.padding(10.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notif.title,
                    fontSize = 15.sp,
                    fontWeight = if (notif.isRead) FontWeight.Normal else FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notif.body,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = notif.createAt,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
    }
}