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

    // SELLER
    @Serializable
    data object SellerHomeScreenRoute : NavKey

    @Serializable
    data object SellerNotifListRoute : NavKey

    @Serializable
    data object SellerOrderListRoute : NavKey

    @Serializable
    data class IncomingOrderDetailRoute(
        val orderId : String
    ) : NavKey {
        companion object {
        }
    }

    //Chat
    @Serializable
    data object BuyerChatListRoute : NavKey

    @Serializable
    data object SellerChatListRoute : NavKey

    @Serializable
    data class ChatDetailRoute(val roomId: String) : NavKey

    // BUYER
    // Keranjang
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
        val notificationId: String,
    ) : NavKey

    // Profile
    @Serializable
    data object ProfileRoute : NavKey
    @Serializable
    data object AddressListRoute : NavKey


    // TRANSACTION
        @Serializable
        data class CheckoutRoute(
            val orderId: String? = null,
            val productId: String? = null,
            val quantity: Int = 1,
            val fromCart: Boolean = true,
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

    // STORE
    @Serializable
    data class StorePageRoute(val storeId: String) : NavKey

}