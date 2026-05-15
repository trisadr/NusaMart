package com.example.nusamart.core

import android.app.Application
import com.example.nusamart.data.repository.cart.CartRepository
import com.example.nusamart.data.repository.notif.NotificationRepository
import com.example.nusamart.data.repository.order.OrderRepository
import com.example.nusamart.data.repository.product.ProductRepository
import com.example.nusamart.data.repository.review.ReviewRepository
import com.example.nusamart.data.repository.shipping.ShippingRepository
import com.example.nusamart.data.repository.store.StoreRepository
import com.example.nusamart.data.repository.transaction.TransactionRepository
import com.example.nusamart.data.repository.user.UserRepository

class MyApplication : Application() {
    // berguna untuk inisialisasi repository repository
    lateinit var userRepository: UserRepository
        private set

    lateinit var storeRepository: StoreRepository
        private set

    lateinit var productRepository: ProductRepository
        private set

    lateinit var orderRepository: OrderRepository
        private set

    lateinit var shippingRepository: ShippingRepository
        private set

    lateinit var  transactionRepository: TransactionRepository
        private set

    lateinit var  cartRepository: CartRepository
        private set

    lateinit var  reviewRepository: ReviewRepository
        private set

    lateinit var   notificationRepository:  NotificationRepository
        private set

    override fun onCreate() {
        super.onCreate()
        userRepository = UserRepository(this)
        storeRepository = StoreRepository(this)
        productRepository = ProductRepository(this)
        orderRepository = OrderRepository(this)
        shippingRepository = ShippingRepository(this)
        transactionRepository = TransactionRepository(this)
        cartRepository = CartRepository(this)
        reviewRepository = ReviewRepository(this)
        notificationRepository = NotificationRepository(this)
    }
}