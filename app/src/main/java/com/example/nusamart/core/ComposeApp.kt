package com.example.nusamart.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.nusamart.feature.auth.LoginScreen
import com.example.nusamart.feature.auth.RegisterScreen
import com.example.nusamart.feature.buyer.cart.CartScreen
import com.example.nusamart.feature.buyer.homepage.HomePageScreen
import com.example.nusamart.feature.buyer.homepage.ProductPageScreen
import com.example.nusamart.feature.buyer.homepage.SearchResultScreen
import com.example.nusamart.feature.buyer.notification.NotificationDetailScreen
import com.example.nusamart.feature.buyer.notification.NotificationScreen
import com.example.nusamart.feature.buyer.order.OrderDetailScreen
import com.example.nusamart.feature.buyer.order.OrderListScreen
import com.example.nusamart.feature.buyer.profile.ProfileScreen
import com.example.nusamart.feature.buyer.review.ReviewScreen
import com.example.nusamart.feature.buyer.transaction.PaymentConfirmationScreen
import com.example.nusamart.feature.buyer.transaction.PaymentScreen
import com.example.nusamart.feature.buyer.transaction.PaymentSuccessScreen
import com.example.nusamart.feature.landingpage.LandingScreen
import com.example.nusamart.ui.theme.NusaMartTheme

@Composable
fun ComposeApp() {
    val backStack = rememberNavBackStack(Routes.LandingPageRoute)
    val activeUserId = "";

    CompositionLocalProvider(LocalBackStack provides backStack) {
        NusaMartTheme {
            NavDisplay(
                backStack = backStack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {

                    // Auth & Lending
                    entry<Routes.LandingPageRoute> { LandingScreen() }
                    entry<Routes.RegisterRoute> { RegisterScreen() }
                    entry<Routes.LoginPageRoute> { LoginScreen() }

                    // Buyer
                    entry<Routes.CartRoute> { CartScreen() }
                    entry<Routes.HomeRoute> { HomePageScreen() }

                    entry<Routes.SearchResultRoute> { route ->
                        SearchResultScreen(initialKeyword = route.keyword)
                    }

                    entry<Routes.ProductPageRoute> { route ->
                        ProductPageScreen(productId = route.productId)
                    }

                    // Notification
                    entry<Routes.NotificationRoute> { NotificationScreen() }

                    entry<Routes.NotificationDetailRoute> { route ->
                        NotificationDetailScreen(notificationId = route.notificationId)
                    }

                    // Profile
                    entry<Routes.ProfileRoute> { ProfileScreen() }

                    entry<Routes.PaymentRoute> { route ->
                        PaymentScreen(
                            orderId = route.orderId,
                            selectedPaymentMethod = route.selectedPaymentMethod
                        )
                    }
                    entry<Routes.PaymentConfirmationRoute> { route ->
                        PaymentConfirmationScreen(orderId = route.orderId)
                    }
                    entry<Routes.PaymentSuccessRoute> { route ->
                        PaymentSuccessScreen(
                            paymentCode = route.paymentCode,
                            orderId = route.orderId
                        )
                    }

                    // Order
                    entry<Routes.OrderListRoute> { OrderListScreen() }

                    entry<Routes.OrderDetailRoute> { route ->
                        OrderDetailScreen(orderId = route.orderId)
                    }

                    // Review
                    entry<Routes.ReviewRoute> { route ->
                        ReviewScreen(orderId = route.orderId)
                    }
                }
            )
        }
    }
}