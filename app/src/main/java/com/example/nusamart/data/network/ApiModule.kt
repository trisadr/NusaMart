package com.example.nusamart.data.network

import com.example.nusamart.data.preference.TokenPrefs
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
        tokenPrefs: TokenPrefs  // inject TokenPrefs buat ambil token
    ): OkHttpClient {
        return RetrofitProvider.provideOkHttpClient(
            tokenProvider = { tokenPrefs.getToken() }
        )
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return RetrofitProvider.provideRetrofit(okHttpClient)
    }

    // Nanti tambah per-feature di sini:
    // @Provides
    // @Singleton
    // fun provideAuthApi(retrofit: Retrofit): AuthApi {
    //     return retrofit.create(AuthApi::class.java)
    // }
}