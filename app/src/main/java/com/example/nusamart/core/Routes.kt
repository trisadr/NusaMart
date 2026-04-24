package com.example.nusamart.core

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object Routes {

    // LANDING PAGE
    @Serializable
    data object LandingPageRoute : NavKey

    // AUTH
    @Serializable
    data object RegisterRoute : NavKey

    @Serializable
    data object LoginPageRoute : NavKey

    // BUYER
    // Cart
    @Serializable
    data object CartRoute : NavKey

    // HomePage
    @Serializable
    data object HomeRoute : NavKey

    @Serializable
    data class SearchResultRoute(
        val keyword: String
    ) : NavKey

    @Serializable
    data class ProductPageRoute(
        val productId: String
    ) : NavKey

    // Notification
    @Serializable
    data object NotificationRoute : NavKey

    @Serializable
    data class NotificationDetailRoute(
        val notificationId: String
    ) : NavKey

    // Profile
    @Serializable
    data object ProfileRoute : NavKey

    // Transaction
    @Serializable
    data class PaymentRoute(
        val orderId: String,
        val selectedPaymentMethod: String? = null  // nullable, diisi setelah dari confirmation
    ) : NavKey

    @Serializable
    data class PaymentConfirmationRoute(
        val orderId: String  // perlu orderId untuk balik ke PaymentRoute dengan method terpilih
    ) : NavKey
    // Tambah route baru
    @Serializable
    data class PaymentSuccessRoute(
        val paymentCode: String,
        val orderId: String
    ) : NavKey
    // Order
    @Serializable
    data object OrderListRoute : NavKey

    @Serializable
    data class OrderDetailRoute(
        val orderId: String
    ) : NavKey

    // Review
    @Serializable
    data class ReviewRoute(
        val orderId: String
    ) : NavKey

    // SELLER (Belum ada)
}