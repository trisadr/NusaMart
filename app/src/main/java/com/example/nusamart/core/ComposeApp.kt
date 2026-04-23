package com.example.nusamart.core

//import com.example.nusamart.feature.screen.CartScreen
//import com.example.nusamart.feature.screen.HomePageScreen
//import com.example.nusamart.feature.screen.LandingScreen
//import com.example.nusamart.feature.screen.OrderDetailScreen
//import com.example.nusamart.feature.screen.OrderListScreen
//import com.example.nusamart.feature.screen.RegisterScreen
//import com.example.nusamart.feature.screen.SearchResultScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.nusamart.database.dummyProductForTesting
import com.example.nusamart.feature.auth.LoginScreen
import com.example.nusamart.feature.screen.CartScreen
//import com.example.nusamart.feature.buyer.homepage.HomePageScreen
//import com.example.nusamart.feature.landingpage.LandingScreen
import com.example.nusamart.feature.screen.HomePageScreen
import com.example.nusamart.feature.screen.LandingScreen
import com.example.nusamart.feature.screen.NotificationDetailScreen
import com.example.nusamart.feature.screen.NotificationScreen
import com.example.nusamart.feature.screen.OrderDetailScreen
import com.example.nusamart.feature.screen.OrderListScreen
import com.example.nusamart.feature.screen.ProductPageScreen
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
                    entry<Routes.CartRoute> {
                        CartScreen(
                            onBackClick = {
                                if (backStack.size > 1) backStack.removeLastOrNull()
                            }
                        )
                    }

                    entry<Routes.HomeRoute> { HomePageScreen() }

                    // [DIPERBAIKI] Hapus kata "entry" dan kurung kurawal yang dobel
//                    entry<Routes.SearchResultRoute> { route ->
//                        SearchResultScreen(initialKeyword = route.keyword)
//                    }

//                    // [DIPERBAIKI] Hapus pemanggilan nama fungsi yang terulang di dalam parameternya
                    entry<Routes.ProductPageRoute> { route ->
                        val product = dummyProductForTesting.getProductById(route.productId) ?: return@entry
                        ProductPageScreen(
                            product = product,
                            onBackClick = {
                                if (backStack.size > 1) backStack.removeLastOrNull()
                            }
                        )
                    }

//                     --- Notification ---
                    entry<Routes.NotificationRoute> {
                        NotificationScreen(
                            onBackClick = {
                                if (backStack.size > 1) backStack.removeLastOrNull()
                            },
                            onNavigateToCart = {
                                backStack.add(Routes.CartRoute)
                            },
                            onNavigateToProduct = { productId, title, content ->
                                backStack.add(
                                    Routes.NotificationDetailRoute(
                                        title = title,
                                        content = content,
                                        productId = productId
                                    )
                                )
                            },
                            onNavigateToOrder = { orderId ->
                                backStack.add(Routes.OrderDetailRoute(orderId))
                            }
                        )
                    }

                    // Membaca notificationId yang dikirim melalui Routes
                    entry<Routes.NotificationDetailRoute> { route ->
                        val productId = route.productId ?: return@entry
                        val product = dummyProductForTesting.getProductById(productId) ?: return@entry

                        NotificationDetailScreen(
                            title = route.title,
                            content = route.content,
                            product = product,
                            onBackClick = {
                                if (backStack.size > 1) backStack.removeLastOrNull()
                            },
                            onNavigateToProduct = { id ->
                                backStack.add(Routes.ProductPageRoute(id))
                            }
                        )
                    }

                    // --- Profile ---
//                    entry<Routes.ProfileRoute> { ProfileScreen() }

//                    // --- Transaction ---
//                    entry<Routes.PaymentRoute> { PaymentScreen() }
//                    entry<Routes.PaymentConfirmationRoute> { PaymentConfirmationScreen() }

//                    // --- Order ---
                    entry<Routes.OrderListRoute> { OrderListScreen() }

                    // Membaca orderId yang dikirim melalui Routes
                    entry<Routes.OrderDetailRoute> { route ->
                        OrderDetailScreen(
                            orderId = route.orderId,
                            onBackClick = {
                                if (backStack.size > 1) backStack.removeLastOrNull()
                            }
                        )
                    }

//                    // --- Review ---
//                    entry<Routes.ReviewRoute> { ReviewScreen() }
                }
            )
        }
    }
}