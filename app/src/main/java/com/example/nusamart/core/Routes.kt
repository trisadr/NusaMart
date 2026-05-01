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
        val notificationId: String
    ) : NavKey

    // --- Profile ---
    @Serializable
    data object ProfileRoute : NavKey

    // --- Transaction ---
    @Serializable
    data class PaymentRoute(
        // dari CartScreen — orderId sudah ada di storage
        val orderId: String? = null,

        // dari ProductPage — order belum dibuat, bawa data produknya
        val productId: String? = null,
        val quantity: Int = 1,
        val fromCart: Boolean = true,

        val selectedPaymentMethod: String? = null
    ) : NavKey

    @Serializable
    data class PaymentConfirmationRoute(
        val orderId: String? = null,
        val productId: String? = null,
        val quantity: Int = 1,
        val fromCart: Boolean = true  // perlu orderId untuk balik ke PaymentRoute
    ) : NavKey
    
    @Serializable
    data class PaymentSuccessRoute(
        val paymentCode: String,
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
