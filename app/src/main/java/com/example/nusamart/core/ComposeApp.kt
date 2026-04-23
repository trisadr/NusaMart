package com.example.nusamart.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.nusamart.feature.auth.LoginScreen
import com.example.nusamart.feature.buyer.homepage.HomePageScreen
import com.example.nusamart.feature.buyer.homepage.ProductPageScreen
import com.example.nusamart.feature.landingpage.LandingScreen
import com.example.nusamart.ui.theme.NusaMartTheme

@Composable
fun ComposeApp() {
    val backStack = rememberNavBackStack(Routes.LandingPageRoute)

    // Menyediakan backStack secara global ke seluruh halaman di bawahnya
    CompositionLocalProvider(LocalBackStack provides backStack) {
        NusaMartTheme {
            NavDisplay(
                backStack = backStack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {

                    // ================== AUTH & LANDING ==================
                    entry<Routes.LandingPageRoute> { LandingScreen() }
//                    entry<Routes.RegisterRoute> { RegisterScreen() }
                    entry<Routes.LoginPageRoute> { LoginScreen() }

                    // ================== BUYER ==================
//                    entry<Routes.CartRoute> { CartScreen() }
                    entry<Routes.HomeRoute> { HomePageScreen() }

                    // [DIPERBAIKI] Hapus kata "entry" dan kurung kurawal yang dobel
//                    entry<Routes.SearchResultRoute> { route ->
//                        SearchResultScreen(initialKeyword = route.keyword)
//                    }

                    entry<Routes.ProductPageRoute> { route ->
                        ProductPageScreen(productId = route.productId)
                    }

                    // --- Notification ---
//                    entry<Routes.NotificationRoute> { NotificationScreen() }

//                    // Membaca notificationId yang dikirim melalui Routes
//                    entry<Routes.NotificationDetailRoute> { route ->
//                        NotificationDetailScreen(notificationId = route.notificationId)
//                    }

                    // --- Profile ---
//                    entry<Routes.ProfileRoute> { ProfileScreen() }

//                    // --- Transaction ---
//                    entry<Routes.PaymentRoute> { PaymentScreen() }
//                    entry<Routes.PaymentConfirmationRoute> { PaymentConfirmationScreen() }

//                    // --- Order ---
//                    entry<Routes.OrderListRoute> { OrderListScreen() }

                    // Membaca orderId yang dikirim melalui Routes
//                    entry<Routes.OrderDetailRoute> { route ->
//                        OrderDetailScreen(orderId = route.orderId)
//                    }

//                    // --- Review ---
//                    entry<Routes.ReviewRoute> { ReviewScreen() }
                }
            )
        }
    }
}