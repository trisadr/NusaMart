package com.example.nusamart.data.network

import com.example.nusamart.data.preference.TokenPrefs
import com.example.nusamart.feature.auth.login.AuthAndUserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
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

    // Tambahkan baris ini agar AuthAndUserApi bisa dipakai di seluruh aplikasi
    @Provides
    @Singleton
    fun provideAuthAndUserApi(retrofit: Retrofit): AuthAndUserApi {
        return retrofit.create(AuthAndUserApi::class.java)
    }
}