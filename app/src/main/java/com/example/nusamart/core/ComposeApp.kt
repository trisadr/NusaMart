package com.example.nusamart.core

//import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
//import androidx.navigation3.ui.NavDisplay
import com.example.nusamart.feature.auth.LandingPageScreen
import com.example.nusamart.feature.screen.CartScreen
import com.example.nusamart.feature.screen.HomePageScreen
import com.example.nusamart.feature.screen.NotificationDetailScreen
import com.example.nusamart.feature.screen.NotificationScreen
import com.example.nusamart.feature.screen.OrderDetailScreen
import com.example.nusamart.feature.screen.OrderListScreen
import com.example.nusamart.feature.screen.PaymentConfirmationScreen
import com.example.nusamart.feature.screen.PaymentScreen
import com.example.nusamart.feature.screen.ProductPageScreen
import com.example.nusamart.feature.screen.ProfileScreen
import com.example.nusamart.feature.screen.RegisterScreen
import com.example.nusamart.feature.screen.ReviewScreen
import com.example.nusamart.feature.screen.SearchResultScreen
import com.example.nusamart.ui.theme.NusaMartTheme

//package com.example.nusamart.core

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay

// Pastikan import ini disesuaikan dengan lokasi theme dan screen kamu
import com.example.nusamart.ui.theme.NusaMartTheme
import com.example.nusamart.feature.auth.LandingPageScreen
import com.example.nusamart.feature.auth.LoginScreen
import com.example.nusamart.feature.screen.* // Asumsi semua screen ada di package ini

@Composable
fun ComposeApp() {
    // LandingPageRoute dijadikan layar pertama yang muncul saat app dibuka
    val backStack = rememberNavBackStack(Routes.LandingPageRoute)

    // CompositionLocalProvider dihapus sementara agar tidak error,
    // karena kita bisa memanggil backStack langsung di dalam file ini.

    NusaMartTheme {
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                // Menyimpan state composable saat navigasi
                rememberSaveableStateHolderNavEntryDecorator(),
                // Mengelola ViewModel per layar
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {

                // ================== LANDING PAGE ==================
                entry<Routes.LandingPageRoute> {
                    LandingScreen(
                        onNavigateToLogin = {
                            // Hapus LandingPage dari history agar saat user tekan 'Back' di Login,
                            // tidak kembali lagi ke halaman loading (langsung keluar app)
                            backStack.clear()

                            // Tambahkan halaman Login ke layar
                            backStack.add(Routes.LoginPageRoute)
                        }
                    )
                }

                // ================== AUTH ==================
                entry<Routes.RegisterRoute> { RegisterScreen() }
                // Pastikan memanggil LoginScreen(), bukan LoginPageScreen()
                entry<Routes.LoginPageRoute> { LoginScreen() }

                // ================== BUYER ==================

                // --- Keranjang ---
                entry<Routes.CartRoute> { CartScreen() }

                // --- HomePage ---
                entry<Routes.HomeRoute> { HomePageScreen() }
                entry<Routes.SearchResultRoute> { SearchResultScreen() }

                // Membaca productId yang dikirim melalui Routes
                entry<Routes.ProductPageRoute> { route ->
                    ProductPageScreen(productId = route.productId)
                }

                // --- Notification ---
                entry<Routes.NotificationRoute> { NotificationScreen() }

                // Membaca notificationId yang dikirim melalui Routes
                entry<Routes.NotificationDetailRoute> { route ->
                    NotificationDetailScreen(notificationId = route.notificationId)
                }

                // --- Profile ---
                entry<Routes.ProfileRoute> { ProfileScreen() }

                // --- Transaction ---
                entry<Routes.PaymentRoute> { PaymentScreen() }
                entry<Routes.PaymentConfirmationRoute> { PaymentConfirmationScreen() }

                // --- Order ---
                entry<Routes.OrderListRoute> { OrderListScreen() }

                // Membaca orderId yang dikirim melalui Routes
                entry<Routes.OrderDetailRoute> { route ->
                    OrderDetailScreen(orderId = route.orderId)
                }

                // --- Review ---
                entry<Routes.ReviewRoute> { ReviewScreen() }

                // ================== SELLER ==================
                // Tambahkan screen seller di sini nantinya
            }
        )
    }
}