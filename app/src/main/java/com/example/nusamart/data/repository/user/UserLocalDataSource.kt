package com.example.nusamart.data.repository.user

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

// Representasi seluruh isi users.json
data class UserDatabase(
    val users: MutableList<UserJson> = mutableListOf(),
    val sellers: MutableList<SellerJson> = mutableListOf(),
    val addresses: MutableList<UserAddressJson> = mutableListOf(),
    val sessions: MutableList<SessionJson> = mutableListOf()
)

// JSON-friendly version (pakai String untuk role & datetime)
data class UserJson(
    val idUser: String,
    val username: String,
    val email: String,
    val passwordHashed: String,
    val phone: String,
    val role: String,
    val createAt: String,
    val updateAt: String,
    val imageURL: Int? = null
)

data class SellerJson(
    val idSeller: String,
    val nik: String,
    val ktpPhoto: Int,
    val bankName: String,
    val accountNumber: String
)

data class UserAddressJson(
    val idAddress: String,
    val idUser: String,
    val label: String,
    val receiver: String,
    val phone: String,
    val completeAddress: String,
    val city: String,
    val province: String,
    val postalCode: String,
    val isDefault: Boolean
)

data class SessionJson(
    val idUser: String,
    val token: String,
    val role: String,
    val loginAt: String
)

class UserLocalDataSource(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val fileName = "users.json"

    // File di filesDir — ini yang bisa ditulis
    private val localFile: File
        get() = File(context.filesDir, fileName)

    // ── Setup awal ────────────────────────────────────────────────────────────
    // Dipanggil satu kali saat app pertama dibuka.
    // Kalau users.json belum ada di filesDir, copy dari assets/.
    fun initializeIfNeeded() {
        if (!localFile.exists()) {
            val seedJson = context.assets.open(fileName).bufferedReader().readText()
            localFile.writeText(seedJson)
        }
    }

    // ── Baca & tulis seluruh database ─────────────────────────────────────────
    fun readDatabase(): UserDatabase {
        initializeIfNeeded()
        val json = localFile.readText()
        return gson.fromJson(json, UserDatabase::class.java) ?: UserDatabase()
    }

    fun writeDatabase(db: UserDatabase) {
        val json = gson.toJson(db)
        localFile.writeText(json)
    }
}