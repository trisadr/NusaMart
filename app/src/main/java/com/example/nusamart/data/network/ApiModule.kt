package com.example.nusamart.data.network

import com.example.nusamart.data.interfaceapi.CartApi
import com.example.nusamart.data.interfaceapi.ChatApi
import com.example.nusamart.data.interfaceapi.NotificationApi
import com.example.nusamart.data.interfaceapi.OrderApi
import com.example.nusamart.data.interfaceapi.PaymentApi
import com.example.nusamart.data.interfaceapi.ProductApi
import com.example.nusamart.data.interfaceapi.ReviewApi
import com.example.nusamart.data.interfaceapi.ShippingApi
import com.example.nusamart.data.interfaceapi.StoreApi
import com.example.nusamart.data.interfaceapi.WalletApi
import com.example.nusamart.data.preference.TokenPrefs
import com.example.nusamart.feature.auth.login.AuthAndUserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenPrefs: TokenPrefs
    ): OkHttpClient {
        return RetrofitProvider.provideOkHttpClient(
            tokenProvider = {
                kotlinx.coroutines.runBlocking {
                    tokenPrefs.getTokenSync()
                }
            }
        )
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return RetrofitProvider.provideRetrofit(okHttpClient)
    }

    // Auth & User API
    @Provides
    @Singleton
    fun provideAuthAndUserApi(retrofit: Retrofit): AuthAndUserApi {
        return retrofit.create(AuthAndUserApi::class.java)
    }

    // Store API
    @Provides
    @Singleton
    fun provideStoreApi(retrofit: Retrofit): StoreApi {
        return retrofit.create(StoreApi::class.java)
    }

    // Product API (Baris yang baru ditambahkan)
    @Provides
    @Singleton
    fun provideProductApiService(retrofit: Retrofit): ProductApi {
        return retrofit.create(ProductApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCartApiService(retrofit: Retrofit): CartApi {
        return retrofit.create(CartApi::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentApi(retrofit: Retrofit): PaymentApi {
        return retrofit.create(PaymentApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWalletApi(retrofit: Retrofit): WalletApi {
        return retrofit.create(WalletApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOrderApi(retrofit: Retrofit): OrderApi {
        return retrofit.create(OrderApi::class.java)
    }

    @Provides
    @Singleton
    fun provideShippingApi(retrofit: Retrofit): ShippingApi {
        return retrofit.create(ShippingApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReviewApi(retrofit: Retrofit): ReviewApi {
        return retrofit.create(ReviewApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChatApi(retrofit: Retrofit): ChatApi {
        return retrofit.create(ChatApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }
}