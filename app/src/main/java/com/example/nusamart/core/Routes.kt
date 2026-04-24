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

    // Catatan: Logout biasanya berupa fungsi/action, bukan screen tersendiri.
    // Namun jika ada screen khusus untuk proses logout, bisa ditambahkan:
    // @Serializable data object LogoutRoute : NavKey

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
    data object PaymentRoute : NavKey

    @Serializable
    data object PaymentConfirmationRoute : NavKey

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

    // ================== SELLER ==================
    // Tambahkan route seller di sini nanti saat screen-nya sudah didefinisikan
}