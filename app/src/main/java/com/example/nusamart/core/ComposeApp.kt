package com.example.nusamart.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.nusamart.feature.auth.login.LoginScreen
import com.example.nusamart.feature.auth.register.RegisterScreen
import com.example.nusamart.feature.buyer.cart.CartScreen
import com.example.nusamart.feature.buyer.homepage.home.HomePageScreen
import com.example.nusamart.feature.buyer.homepage.product.ProductPageScreen
import com.example.nusamart.feature.buyer.homepage.search.SearchResultScreen
import com.example.nusamart.feature.buyer.notification.detail.NotificationDetailScreen
import com.example.nusamart.feature.buyer.notification.list.NotificationScreen
import com.example.nusamart.feature.buyer.order.detail.OrderDetailScreen
import com.example.nusamart.feature.buyer.order.list.OrderListScreen
import com.example.nusamart.feature.buyer.profile.address.AddressScreen
import com.example.nusamart.feature.buyer.profile.mainprofile.ProfileScreen
import com.example.nusamart.feature.buyer.review.ReviewScreen
import com.example.nusamart.feature.buyer.transaction.address.AddressOptionScreen
import com.example.nusamart.feature.buyer.transaction.checkout.CheckoutScreen
import com.example.nusamart.feature.buyer.transaction.courier.CourierOptionScreen
import com.example.nusamart.feature.buyer.transaction.payment.PaymentOptionScreen
import com.example.nusamart.feature.buyer.transaction.success.CheckoutSuccessScreen
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
                    entry<Routes.RegisterRoute> { RegisterScreen() }
                    entry<Routes.LoginPageRoute> { LoginScreen() }

                    // ================== SELLER ==================
//                    entry<Routes.SellerHomeRoute> {
//                        Text("Ini Halaman Beranda Penjual")
//                    }

                    // ================== BUYER ==================

                    // --- Cart & Home ---
                    entry<Routes.CartRoute> { CartScreen() }
                    entry<Routes.HomeRoute> { HomePageScreen() }

                    entry<Routes.SearchResultRoute> { route ->
                        SearchResultScreen(initialKeyword = route.keyword)
                    }

                    entry<Routes.ProductPageRoute> { route ->
                        ProductPageScreen(productId = route.productId)
                    }

                    // --- Notification ---
                    entry<Routes.NotificationRoute> { NotificationScreen() }

                    entry<Routes.NotificationDetailRoute> { route ->
                        NotificationDetailScreen(notificationId = route.notificationId)
                    }

                    // --- Profile & Address ---
                    entry<Routes.ProfileRoute> { ProfileScreen() }
                    entry<Routes.AddressListRoute> { AddressScreen() }

                    // --- Transaction / Checkout Flow ---
                    entry<Routes.CheckoutRoute> { route ->
                        CheckoutScreen(route = route)
                    }

                    entry<Routes.AddressOptionRoute> { route ->
                        AddressOptionScreen(currentRoute = route.checkoutData)
                    }

                    entry<Routes.CourierOptionRoute> { route ->
                        CourierOptionScreen(currentRoute = route.checkoutData)
                    }

                    entry<Routes.PaymentOptionRoute> { route ->
                        PaymentOptionScreen(currentRoute = route.checkoutData)
                    }

                    entry<Routes.CheckoutSuccessRoute> { route ->
                        CheckoutSuccessScreen(
                            paymentId = route.paymentId,
                            orderId = route.orderId
                        )
                    }

                    // --- Order History ---
                    entry<Routes.OrderListRoute> { OrderListScreen() }

                    entry<Routes.OrderDetailRoute> { route ->
                        OrderDetailScreen(orderId = route.orderId)
                    }

                    // --- Review ---
                    entry<Routes.ReviewRoute> { route ->
                        ReviewScreen(orderId = route.orderId)
                    }
                }
            )
        }
    }
}