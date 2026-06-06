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
import com.example.nusamart.feature.components.SellerBottomMenu
import com.example.nusamart.feature.components.SellerBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerChatListScreen() {
    val backStack = LocalBackStack.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat dengan Pembeli") },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            SellerBottomNavigation(
                selectedMenu = SellerBottomMenu.CHAT,
                onMenuSelected = { menu ->
                    when (menu) {
                        SellerBottomMenu.HOME -> backStack.add(Routes.SellerHomeScreenRoute)
                        SellerBottomMenu.ORDER -> backStack.add(Routes.SellerOrderListRoute)
                        SellerBottomMenu.CHAT -> Unit
                        SellerBottomMenu.NOTIFICATION -> backStack.add(Routes.SellerNotifListRoute)
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