package com.example.nusamart.feature.chat.list

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.feature.components.BottomMenu
import com.example.nusamart.feature.components.NusaMartBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerChatListScreen() {
    val backStack = LocalBackStack.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pesan") },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            NusaMartBottomNavigation(
                selectedMenu = BottomMenu.HOME,
                onMenuSelected = { menu ->
                    when (menu) {
                        BottomMenu.HOME -> backStack.add(Routes.HomeRoute)
                        BottomMenu.NOTIFICATION -> backStack.add(Routes.NotificationRoute)
                        BottomMenu.PROFILE -> backStack.add(Routes.ProfileRoute)
                        BottomMenu.CART -> backStack.add(Routes.CartRoute)
                    }
                }
            )
        }
    ) { padding ->
        ChatListContent(
            paddingValues = padding,
            onRoomClick = { roomId -> backStack.add(Routes.ChatDetailRoute(roomId)) }
        )
    }
}