package com.example.nusamart.data.repository.user

import android.content.Context
import com.example.nusamart.data.preference.UserPreference
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

// --- Model Tambahan ---
data class UserJson(
    val idUser: String,
    val username: String,
    val email: String,
    val password: String,
    val phone: String,
    val role: String,
    val createAt: String,
    val updateAt: String,
    val imageURL: Int? = null
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

// --- Hasil Operasi ---
sealed class RegisterResult {
    object Success : RegisterResult()
    data class ErrorDuplicate(val message: String) : RegisterResult()
}

sealed class LoginResult {
    data class Success(val role: String, val userId: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

// --- Repository ---
@Singleton
class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreference: UserPreference
) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    // =========================================================================
    // PERBAIKAN FATAL CRASH: Menggunakan fungsi baca spesifik dengan TypeToken yang pasti
    // =========================================================================

    private fun readUserJson(): MutableList<UserJson> {
        val file = File(context.filesDir, "user.json")
        if (!file.exists()) {
            try {
                context.assets.open("user.json").use { inputStream ->
                    file.writeText(inputStream.bufferedReader().readText())
                }
            } catch (e: Exception) { return mutableListOf() }
        }
        return try {
            val json = file.readText()
            if (json.isBlank()) return mutableListOf()
            val type = object : TypeToken<List<UserJson>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) { mutableListOf() }
    }

    private fun readSellerJson(): MutableList<SellerJson> {
        val file = File(context.filesDir, "seller.json")
        if (!file.exists()) {
            try {
                context.assets.open("seller.json").use { inputStream ->
                    file.writeText(inputStream.bufferedReader().readText())
                }
            } catch (e: Exception) { return mutableListOf() }
        }
        return try {
            val json = file.readText()
            if (json.isBlank()) return mutableListOf()
            val type = object : TypeToken<List<SellerJson>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) { mutableListOf() }
    }

    private fun readAddressJson(): MutableList<UserAddressJson> {
        val file = File(context.filesDir, "userAddress.json")
        if (!file.exists()) {
            try {
                context.assets.open("userAddress.json").use { inputStream ->
                    file.writeText(inputStream.bufferedReader().readText())
                }
            } catch (e: Exception) { return mutableListOf() }
        }
        return try {
            val json = file.readText()
            if (json.isBlank()) return mutableListOf()
            val type = object : TypeToken<List<UserAddressJson>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) { mutableListOf() }
    }

    private fun writeJson(fileName: String, data: Any) {
        try {
            val file = File(context.filesDir, fileName)
            file.writeText(gson.toJson(data))
        } catch (e: Exception) { e.printStackTrace() }
    }

    // =========================================================================

    // Session DataStore
    suspend fun getActiveUserId(): String? {
        val session = userPreference.getSession().first()
        return if (session.isLogin) session.userId else null
    }

    suspend fun getActiveUserRole(): String? {
        val session = userPreference.getSession().first()
        return if (session.isLogin) session.role else null
    }

    // Login & Logout
    suspend fun login(emailOrUsername: String, password: String): LoginResult = withContext(Dispatchers.IO) {
        try {
            delay(1000)
            val users = readUserJson()

            val matchedUser = users.find {
                ((it.email ?: "").lowercase().trim() == emailOrUsername.lowercase().trim() ||
                        (it.username ?: "").lowercase().trim() == emailOrUsername.lowercase().trim()) &&
                        (it.password ?: "").trim() == password.trim()
            }

            if (matchedUser != null) {
                userPreference.saveSession(userId = matchedUser.idUser, role = matchedUser.role)
                return@withContext LoginResult.Success(role = matchedUser.role, userId = matchedUser.idUser)
            } else {
                return@withContext LoginResult.Error("Username/email atau password salah.")
            }
        } catch (e: Exception) {
            return@withContext LoginResult.Error("Gagal login: ${e.message}")
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        userPreference.logout()
    }

    // Fitur Profile & Address
    suspend fun getCurrentUser(): UserJson? = withContext(Dispatchers.IO) {
        val activeUserId = getActiveUserId() ?: return@withContext null
        return@withContext readUserJson().find { it.idUser == activeUserId }
    }

    suspend fun getCurrentSeller(): SellerJson? = withContext(Dispatchers.IO) {
        val activeUserId = getActiveUserId() ?: return@withContext null
        return@withContext readSellerJson().find { it.idSeller == activeUserId }
    }

    suspend fun getUserAddresses(): List<UserAddressJson> = withContext(Dispatchers.IO) {
        val activeUserId = getActiveUserId() ?: return@withContext emptyList()
        return@withContext readAddressJson().filter { it.idUser == activeUserId }
    }

    suspend fun addAddress(label: String, receiver: String, phone: String, completeAddress: String, city: String, province: String, postalCode: String, isDefault: Boolean) = withContext(Dispatchers.IO) {
        val activeUserId = getActiveUserId() ?: return@withContext
        val addresses = readAddressJson()

        if (isDefault) {
            for (i in addresses.indices) {
                if (addresses[i].idUser == activeUserId) addresses[i] = addresses[i].copy(isDefault = false)
            }
        }

        val maxIdNum = addresses.maxOfOrNull { it.idAddress.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
        val newId = String.format("ADR-%06d", maxIdNum + 1)

        addresses.add(UserAddressJson(newId, activeUserId, label, receiver, phone, completeAddress, city, province, postalCode, isDefault))
        writeJson("userAddress.json", addresses)
    }

    suspend fun updateAddress(addressId: String, label: String, receiver: String, phone: String, completeAddress: String, city: String, province: String, postalCode: String, isDefault: Boolean) = withContext(Dispatchers.IO) {
        val activeUserId = getActiveUserId() ?: return@withContext
        val addresses = readAddressJson()

        if (isDefault) {
            for (i in addresses.indices) {
                if (addresses[i].idUser == activeUserId && addresses[i].idAddress != addressId) addresses[i] = addresses[i].copy(isDefault = false)
            }
        }

        val index = addresses.indexOfFirst { it.idAddress == addressId }
        if (index != -1) {
            addresses[index] = addresses[index].copy(label = label, receiver = receiver, phone = phone, completeAddress = completeAddress, city = city, province = province, postalCode = postalCode, isDefault = isDefault)
            writeJson("userAddress.json", addresses)
        }
    }

    suspend fun deleteAddress(addressId: String) = withContext(Dispatchers.IO) {
        val addresses = readAddressJson()
        addresses.removeAll { it.idAddress == addressId }
        writeJson("userAddress.json", addresses)
    }

    // Register yang Dibungkus Try-Catch (Anti-Crash)
    suspend fun register(
        username: String, email: String, phone: String, password: String, isSeller: Boolean,
        nik: String? = null, bankName: String? = null, accountNumber: String? = null
    ): RegisterResult = withContext(Dispatchers.IO) {
        try {
            delay(1000)
            val users = readUserJson()

            if (users.any { (it.username ?: "").lowercase().trim() == username.lowercase().trim() }) {
                return@withContext RegisterResult.ErrorDuplicate("Username sudah digunakan.")
            }
            if (users.any { (it.email ?: "").lowercase().trim() == email.lowercase().trim() }) {
                return@withContext RegisterResult.ErrorDuplicate("Email sudah terdaftar.")
            }

            val prefix = if (isSeller) "SLR-" else "BYR-"
            val filteredUsers = users.filter { (it.idUser ?: "").startsWith(prefix) }
            val maxIdNum = filteredUsers.maxOfOrNull { (it.idUser ?: "").substringAfter("-").toIntOrNull() ?: 0 } ?: 0
            val newId = String.format("%s%06d", prefix, maxIdNum + 1)
            val now = LocalDateTime.now().toString()
            val role = if (isSeller) "SELLER" else "BUYER"

            users.add(UserJson(newId, username.trim(), email.trim(), password.trim(), phone.trim(), role, now, now))
            writeJson("user.json", users)

            if (isSeller) {
                val sellers = readSellerJson()
                sellers.add(
                    SellerJson(
                        idSeller = newId,
                        nik = nik?.trim() ?: "",
                        bankName = bankName?.trim() ?: "",
                        accountNumber = accountNumber?.trim() ?: ""
                    )
                )
                writeJson("seller.json", sellers)
            }

            return@withContext RegisterResult.Success

        } catch (e: Exception) {
            e.printStackTrace()
            // Kalau ada error tidak akan force close, melainkan mengirim peringatan ke layar HP
            return@withContext RegisterResult.ErrorDuplicate("Gagal memproses data: ${e.message}")
        }
    }

    // Pengecekan Seller (Opsional, jika masih dipakai)
    suspend fun isSellerDataComplete(userId: String): Boolean = withContext(Dispatchers.IO) {
        val sellers = readSellerJson()
        return@withContext sellers.any { it.idSeller == userId }
    }

    suspend fun completeSellerData(userId: String, nik: String, bankName: String, accountNumber: String): Boolean = withContext(Dispatchers.IO) {
        val sellers = readSellerJson()
        if (sellers.none { it.idSeller == userId }) {
            sellers.add(SellerJson(idSeller = userId, nik = nik.trim(), bankName = bankName.trim(), accountNumber = accountNumber.trim()))
            writeJson("seller.json", sellers)
        }
        return@withContext true
    }
}