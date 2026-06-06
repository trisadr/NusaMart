package com.example.nusamart.feature.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// ==========================================
// 1. ENUM DAN NAVIGASI UNTUK PEMBELI (BUYER)
// ==========================================

enum class BottomMenu {
    HOME,
    NOTIFICATION,
    CART,
    PROFILE
}

@Composable
fun NusaMartBottomNavigation(
    selectedMenu: BottomMenu?,
    onMenuSelected: (BottomMenu) -> Unit
) {
    NavigationBar(
        modifier = Modifier.height(65.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets(0.dp)
    ) {
        val itemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
            label = { Text("Beranda") },
            selected = selectedMenu == BottomMenu.HOME,
            onClick = { onMenuSelected(BottomMenu.HOME) },
            colors = itemColors
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Notifikasi") },
            label = { Text("Notifikasi") },
            selected = selectedMenu == BottomMenu.NOTIFICATION,
            onClick = { onMenuSelected(BottomMenu.NOTIFICATION) },
            colors = itemColors
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Keranjang") },
            label = { Text("Keranjang") },
            selected = selectedMenu == BottomMenu.CART,
            onClick = { onMenuSelected(BottomMenu.CART) },
            colors = itemColors
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Saya") },
            label = { Text("Saya") },
            selected = selectedMenu == BottomMenu.PROFILE,
            onClick = { onMenuSelected(BottomMenu.PROFILE) },
            colors = itemColors
        )
    }
}

// ==========================================
// 2. ENUM DAN NAVIGASI UNTUK PENJUAL (SELLER)
// ==========================================

enum class SellerBottomMenu {
    HOME,
    ORDER,
    CHAT,
    NOTIFICATION
}

@Composable
fun SellerBottomNavigation(
    selectedMenu: SellerBottomMenu?,
    onMenuSelected: (SellerBottomMenu) -> Unit
) {
    NavigationBar(
        modifier = Modifier.height(65.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets(0.dp)
    ) {
        val itemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
            label = { Text("Beranda") },
            selected = selectedMenu == SellerBottomMenu.HOME,
            onClick = { onMenuSelected(SellerBottomMenu.HOME) },
            colors = itemColors
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ListAlt, contentDescription = "Pesanan") },
            label = { Text("Pesanan") },
            selected = selectedMenu == SellerBottomMenu.ORDER,
            onClick = { onMenuSelected(SellerBottomMenu.ORDER) },
            colors = itemColors
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Chat, contentDescription = "Chat") },
            label = { Text("Chat") },
            selected = selectedMenu == SellerBottomMenu.CHAT,
            onClick = { onMenuSelected(SellerBottomMenu.CHAT) },
            colors = itemColors
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Campaign, contentDescription = "Notifikasi") },
            label = { Text("Notifikasi") },
            selected = selectedMenu == SellerBottomMenu.NOTIFICATION,
            onClick = { onMenuSelected(SellerBottomMenu.NOTIFICATION) },
            colors = itemColors
        )
    }
}