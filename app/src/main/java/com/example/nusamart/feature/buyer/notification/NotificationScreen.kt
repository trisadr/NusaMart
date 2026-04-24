package com.example.nusamart.feature.buyer.notification

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.entity.Notification
import com.example.nusamart.feature.components.BottomMenu
import com.example.nusamart.feature.components.NusaMartBottomNavigation
import com.example.nusamart.ui.theme.BlackText
import com.example.nusamart.ui.theme.BluePrimary
import com.example.nusamart.ui.theme.GrayBackground
import com.example.nusamart.ui.theme.NusaMartTheme
import com.example.nusamart.ui.theme.OrangePrimary
import com.example.nusamart.ui.theme.WhiteSurface
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun loadNotifications(context: Context): List<Notification> {
    return try {
        val jsonString = context.assets.open("notification.json")
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<List<Notification>>() {}.type
        Gson().fromJson(jsonString, type) ?: emptyList()
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

@Composable
fun NotificationScreen() {
    val context = LocalContext.current
    val backStack = LocalBackStack.current

    val notifications = loadNotifications(context)

    Content(
        notifications = notifications,

        onBackClick = {
            if (backStack.isNotEmpty()) {
                backStack.removeAt(backStack.lastIndex)
            }
        },

        onNotificationClick = { notificationId ->
            backStack.add(Routes.NotificationDetailRoute(notificationId))
        },

        onMenuSelected = { menu ->
            when (menu) {
                BottomMenu.HOME -> {
                    backStack.add(Routes.HomeRoute)
                }

                BottomMenu.NOTIFICATION -> Unit

                BottomMenu.PROFILE -> {
                    backStack.add(Routes.ProfileRoute)
                }

                BottomMenu.CART -> {
                    backStack.add(Routes.CartRoute)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    notifications: List<Notification>,
    onBackClick: () -> Unit,
    onNotificationClick: (String) -> Unit,
    onMenuSelected: (BottomMenu) -> Unit
) {
    val promoNotifications = notifications.filter { it.type == "promo" }
    val orderNotifications = notifications.filter { it.type == "order" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifikasi",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, null, tint = BlackText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteSurface
                )
            )
        },
        bottomBar = {
            NusaMartBottomNavigation(
                selectedMenu = BottomMenu.NOTIFICATION,
                onMenuSelected = onMenuSelected
            )
        },
        containerColor = GrayBackground
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            if (promoNotifications.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.background(WhiteSurface)) {
                        promoNotifications.forEach { notification ->
                            PromoNotificationItem(
                                icon = if (notification.idOrder == null)
                                    Icons.Default.ShoppingCart
                                else
                                    Icons.Default.Storefront,

                                iconBackgroundColor = if (notification.idOrder == null)
                                    BluePrimary
                                else
                                    OrangePrimary,

                                title = notification.title,
                                message = notification.message,
                                date = notification.date,
                                showImageGallery = notification.idOrder != null,
                                onClick = {
                                    onNotificationClick(notification.idNotification)
                                }
                            )
                        }
                    }
                }
            }

            if (orderNotifications.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GrayBackground)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status Pesanan",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Tandai Sudah Dibaca",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.clickable { }
                        )
                    }
                }

                item {
                    Column(modifier = Modifier.background(WhiteSurface)) {
                        orderNotifications.forEachIndexed { index, notification ->
                            OrderNotificationItem(
                                icon = if (index == 0)
                                    Icons.Default.Inventory
                                else
                                    Icons.Default.LocalShipping,

                                title = notification.title,
                                message = notification.message,
                                date = notification.date,
                                onClick = {
                                    onNotificationClick(notification.idNotification)
                                }
                            )

                            if (index < orderNotifications.lastIndex) {
                                HorizontalDivider(
                                    color = GrayBackground,
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(start = 80.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PromoNotificationItem(
    icon: ImageVector,
    iconBackgroundColor: Color,
    title: String,
    message: String,
    date: String,
    showImageGallery: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(4.dp),
                color = iconBackgroundColor
            ) {
                Icon(icon, null, tint = WhiteSurface, modifier = Modifier.padding(10.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = BlackText)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (showImageGallery) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(date, fontSize = 11.sp, color = Color.Gray)
            }

            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.LightGray)
        }

        HorizontalDivider(color = GrayBackground, thickness = 1.dp)
    }
}

@Composable
private fun OrderNotificationItem(
    icon: ImageVector,
    title: String,
    message: String,
    date: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(4.dp),
            color = GrayBackground
        ) {
            Icon(icon, null, tint = Color.Gray, modifier = Modifier.padding(12.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BlackText)

            Spacer(modifier = Modifier.height(4.dp))

            Text(message, fontSize = 13.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(6.dp))

            Text(date, fontSize = 11.sp, color = Color.Gray)
        }

        Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.LightGray)
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationScreenPreview() {
    NusaMartTheme {
        Content(
            notifications = emptyList(),
            onBackClick = {},
            onNotificationClick = {},
            onMenuSelected = {}
        )
    }
}