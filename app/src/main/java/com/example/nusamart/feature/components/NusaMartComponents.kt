package com.example.nusamart.feature.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
            indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp) // Menyesuaikan warna pill indicator agar menyatu dengan background elevasi
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