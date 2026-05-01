package com.example.nusamart.feature.buyer.notification

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.entity.Notification
import com.example.nusamart.ui.theme.NusaMartTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun loadNotificationById(context: Context, notificationId: String): Notification? {
    return try {
        val jsonString = context.assets.open("notification.json")
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<List<Notification>>() {}.type
        val notifications: List<Notification> = Gson().fromJson(jsonString, type) ?: emptyList()

        notifications.find { it.idNotification == notificationId }

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun NotificationDetailScreen(
    notificationId: String
) {
    val context = LocalContext.current
    val backStack = LocalBackStack.current

    val notification = loadNotificationById(context, notificationId)

    if (notification == null) {
        Text("Notification tidak ditemukan")
        return
    }

    Content(
        notification = notification,

        onBackClick = {
            if (backStack.isNotEmpty()) {
                backStack.removeAt(backStack.lastIndex)
            }
        },

        onBottomButtonClick = {
            when (notification.type) {
                "promo" -> {
                    backStack.add(Routes.ProductPageRoute(notification.idOrder ?: ""))
                }

                "order" -> {
                    backStack.add(Routes.OrderDetailRoute(notification.idOrder ?: ""))
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    notification: Notification,
    onBackClick: () -> Unit,
    onBottomButtonClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifikasi") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },

        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = onBottomButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        if (notification.type == "promo")
                            "Lihat Produk"
                        else
                            "Lihat Pesanan"
                    )
                }
            }
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = notification.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notification.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationDetailPreview() {
    val dummy = Notification(
        idNotification = "NOTIF-001",
        idOrder = "PROD-001",
        idStore = null,
        title = "Produk Lagi Laris!",
        message = "Temukan produk terlaris minggu ini dan dapatkan promo menarik.",
        date = "17-04-2026 09:00",
        type = "promo",
        isRead = false
    )

    NusaMartTheme {
        Content(
            notification = dummy,
            onBackClick = {},
            onBottomButtonClick = {}
        )
    }
}