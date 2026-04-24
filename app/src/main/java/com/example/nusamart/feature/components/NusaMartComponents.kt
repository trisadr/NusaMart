package com.example.nusamart.feature.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nusamart.ui.theme.RedPrimary
import com.example.nusamart.ui.theme.WhiteSurface

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
        containerColor = WhiteSurface,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets(0.dp)
    ) {

        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
            label = { Text("Beranda") },
            selected = selectedMenu == BottomMenu.HOME,
            onClick = { onMenuSelected(BottomMenu.HOME) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RedPrimary,
                selectedTextColor = RedPrimary,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = WhiteSurface
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Notifikasi") },
            label = { Text("Notifikasi") },
            selected = selectedMenu == BottomMenu.NOTIFICATION,
            onClick = { onMenuSelected(BottomMenu.NOTIFICATION) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RedPrimary,
                selectedTextColor = RedPrimary,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = WhiteSurface
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Keranjang") },
            label = { Text("Keranjang") },
            selected = selectedMenu == BottomMenu.CART,
            onClick = { onMenuSelected(BottomMenu.CART) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RedPrimary,
                selectedTextColor = RedPrimary,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = WhiteSurface
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Saya") },
            label = { Text("Saya") },
            selected = selectedMenu == BottomMenu.PROFILE,
            onClick = { onMenuSelected(BottomMenu.PROFILE) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RedPrimary,
                selectedTextColor = RedPrimary,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = WhiteSurface
            )
        )
    }
}