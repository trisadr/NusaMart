package com.example.nusamart.core

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object Routes {

    // ================== LANDING PAGE ==================
    @Serializable
    data object LandingPageRoute : NavKey

    // ================== AUTH ==================
    @Serializable

    data object RegisterRoute : NavKey

    @Serializable
    data object LoginPageRoute : NavKey

    // ================== BUYER ==================

    // --- Keranjang ---
    @Serializable
    data object CartRoute : NavKey

    // --- HomePage ---
    @Serializable
    data object HomeRoute : NavKey

    @Serializable
    data class SearchResultRoute(
        val keyword: String
    ) : NavKey

    // Membawa ID Product dari HomePage
    @Serializable
    data class ProductPageRoute(
        val productId: String
    ) : NavKey

    // --- Notification ---
    @Serializable
    data object NotificationRoute : NavKey

    // Membawa ID Notification dari NotificationScreen
    @Serializable
    data class NotificationDetailRoute(
        val title: String,
        val content: String,
        val productId: String? = null
    ) : NavKey

    // --- Profile ---
    @Serializable
    data object ProfileRoute : NavKey
    @Serializable
    data object AddressListRoute : NavKey


// ================== TRANSACTION ==================
    @Serializable
    data class CheckoutRoute(
        val orderId: String? = null,
        val productId: String? = null,
        val quantity: Int = 1,
        val fromCart: Boolean = true,
        // Properti ini menampung pilihan user (akan terisi jika user kembali dari layar opsi)
        val selectedAddressId: String? = null,
        val selectedCourierId: String? = null,
        val selectedPaymentMethodId: String? = null
    ) : NavKey

    @Serializable
    data class AddressOptionRoute(val checkoutData: CheckoutRoute) : NavKey

    @Serializable
    data class CourierOptionRoute(val checkoutData: CheckoutRoute) : NavKey

    @Serializable
    data class PaymentOptionRoute(val checkoutData: CheckoutRoute) : NavKey

    @Serializable
    data class CheckoutSuccessRoute(
        val paymentId: String,
        val orderId: String
    ) : NavKey

    // --- Order ---
    @Serializable
    data object OrderListRoute : NavKey

    // Membawa ID Order dari OrderListScreen
    @Serializable
    data class OrderDetailRoute(
        val orderId: String
    ) : NavKey

    // --- Review ---
    @Serializable
    data class ReviewRoute(
        val orderId: String
    ) : NavKey
}
