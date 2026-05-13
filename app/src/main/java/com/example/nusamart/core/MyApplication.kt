package com.example.nusamart.core

import android.app.Application
import com.example.nusamart.data.repository.user.UserRepository

class MyApplication : Application() {

    // Deklarasi repository yang akan digunakan di seluruh aplikasi
    lateinit var userRepository: UserRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Inisialisasi repository saat aplikasi pertama kali dijalankan.
        // 'this' merujuk pada Context dari aplikasi.
        userRepository = UserRepository(this)

        // Nanti jika ada ProductRepository, SellerRepository, dll
        // Inisialisasinya juga ditambahkan di sini.
    }
}