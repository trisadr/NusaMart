package com.example.nusamart.data.network

import com.example.nusamart.data.interfaceapi.StoreApi
import com.example.nusamart.data.preference.TokenPrefs
import com.example.nusamart.feature.auth.login.AuthAndUserApi
import com.example.nusamart.data.interfaceapi.ProductApi // Pastikan import ini sesuai dengan lokasi file ProductApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenPrefs: TokenPrefs
    ): OkHttpClient {
        return RetrofitProvider.provideOkHttpClient(
            // Token provider otomatis menyisipkan token Sanctum ke header Authorization Bearer
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
    fun provideProductApiService(retrofit: Retrofit): ProductApiService {
        return retrofit.create(ProductApiService::class.java)
    }
}