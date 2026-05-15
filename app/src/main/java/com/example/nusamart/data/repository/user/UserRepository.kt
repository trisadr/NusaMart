package com.example.nusamart.data.repository.user

import android.content.Context
import com.example.nusamart.data.model.user.SellerJson
import com.example.nusamart.data.model.user.UserAddressJson
import com.example.nusamart.data.model.user.UserJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

// Hasil Operasi

sealed class RegisterResult {
    object Success : RegisterResult()
    data class ErrorDuplicate(val message: String) : RegisterResult()
}

sealed class LoginResult {
    data class Success(val role: String, val userId: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

// Repository

class UserRepository(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private var currentActiveUserId: String? = null
    private var currentActiveUserRole: String? = null

    private inline fun <reified T> readJson(fileName: String): MutableList<T> {
        val file = File(context.filesDir, fileName)

        // Cek assets jika file belum ada
        if (!file.exists()) {
            try {
                context.assets.open(fileName).use { inputStream ->
                    val json = inputStream.bufferedReader().readText()
                    file.writeText(json) // Salin dari assets ke internal storage
                }
            } catch (e: Exception) {
                // Jika di assets juga tidak ada, baru kembalikan list kosong
                return mutableListOf()
            }
        }
        val json = file.readText()
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    private fun <T> writeJson(fileName: String, data: List<T>) {
        val file = File(context.filesDir, fileName)
        file.writeText(gson.toJson(data))
    }

    // Fitur Login & Logout

    suspend fun login(emailOrUsername: String, password: String): LoginResult = withContext(Dispatchers.IO) {
        delay(1000)
        val users = readJson<UserJson>("user.json")
        val matchedUser = users.find {
            (it.email.lowercase() == emailOrUsername.lowercase().trim() ||
                    it.username.lowercase() == emailOrUsername.lowercase().trim()) &&
                    it.password == password
        }
        if (matchedUser != null) {
            currentActiveUserId = matchedUser.idUser
            currentActiveUserRole = matchedUser.role
            return@withContext LoginResult.Success(role = matchedUser.role, userId = matchedUser.idUser)
        } else {
            return@withContext LoginResult.Error("Username/email atau password yang kamu masukkan salah. Periksa kembali dan coba lagi.")
        }
    }

    fun logout() {
        currentActiveUserId = null
        currentActiveUserRole = null
    }

    fun getActiveUserId(): String? = currentActiveUserId
    fun getActiveUserRole(): String? = currentActiveUserRole

    // Fitur Profile & Address

    suspend fun getCurrentUser(): UserJson? = withContext(Dispatchers.IO) {
        if (currentActiveUserId == null) return@withContext null
        val users = readJson<UserJson>("user.json")
        return@withContext users.find { it.idUser == currentActiveUserId }
    }

    suspend fun getUserAddresses(): List<UserAddressJson> = withContext(Dispatchers.IO) {
        if (currentActiveUserId == null) return@withContext emptyList()
        val addresses = readJson<UserAddressJson>("userAddress.json")
        return@withContext addresses.filter { it.idUser == currentActiveUserId }
    }

    suspend fun addAddress(
        label: String,
        receiver: String,
        phone: String,
        completeAddress: String,
        city: String,
        province: String,
        postalCode: String,
        isDefault: Boolean
    ) = withContext(Dispatchers.IO) {
        if (currentActiveUserId == null) return@withContext
        val addresses = readJson<UserAddressJson>("userAddress.json")

        if (isDefault) {
            for (i in addresses.indices) {
                if (addresses[i].idUser == currentActiveUserId) {
                    addresses[i] = addresses[i].copy(isDefault = false)
                }
            }
        }

        val maxIdNum = addresses.maxOfOrNull { it.idAddress.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
        val newId = String.format("ADR-%06d", maxIdNum + 1)

        val newAddress = UserAddressJson(
            idAddress = newId,
            idUser = currentActiveUserId!!,
            label = label,
            receiver = receiver,
            phone = phone,
            completeAddress = completeAddress,
            city = city,
            province = province,
            postalCode = postalCode,
            isDefault = isDefault
        )
        addresses.add(newAddress)
        writeJson("userAddress.json", addresses)
    }

    // Fungsi untuk meng-update alamat
    suspend fun updateAddress(
        addressId: String,
        label: String,
        receiver: String,
        phone: String,
        completeAddress: String,
        city: String,
        province: String,
        postalCode: String,
        isDefault: Boolean
    ) = withContext(Dispatchers.IO) {
        if (currentActiveUserId == null) return@withContext
        val addresses = readJson<UserAddressJson>("userAddress.json")

        if (isDefault) {
            for (i in addresses.indices) {
                if (addresses[i].idUser == currentActiveUserId && addresses[i].idAddress != addressId) {
                    addresses[i] = addresses[i].copy(isDefault = false)
                }
            }
        }

        val index = addresses.indexOfFirst { it.idAddress == addressId }
        if (index != -1) {
            addresses[index] = addresses[index].copy(
                label = label,
                receiver = receiver,
                phone = phone,
                completeAddress = completeAddress,
                city = city,
                province = province,
                postalCode = postalCode,
                isDefault = isDefault
            )
            writeJson("userAddress.json", addresses)
        }
    }

    suspend fun deleteAddress(addressId: String) = withContext(Dispatchers.IO) {
        val addresses = readJson<UserAddressJson>("userAddress.json")
        addresses.removeAll { it.idAddress == addressId }
        writeJson("userAddress.json", addresses)
    }

    // Fitur Register

    suspend fun register(
        username: String,
        email: String,
        phone: String,
        password: String,
        isSeller: Boolean
    ): RegisterResult = withContext(Dispatchers.IO) {
        delay(1000)
        val userFile = "user.json"
        val sellerFile = "seller.json"
        val users = readJson<UserJson>(userFile)
        val usernameLower = username.lowercase().trim()
        val emailLower = email.lowercase().trim()
        if (users.any { it.username.lowercase() == usernameLower }) {
            return@withContext RegisterResult.ErrorDuplicate("Username \"$username\" sudah digunakan. Silakan pilih username lain.")
        }
        if (users.any { it.email.lowercase() == emailLower }) {
            return@withContext RegisterResult.ErrorDuplicate("Email \"$email\" sudah terdaftar. Silakan gunakan email lain atau login.")
        }
        val prefix = if (isSeller) "SLR-" else "BYR-"
        val filteredUsers = users.filter { it.idUser.startsWith(prefix) }
        val maxIdNum = filteredUsers.maxOfOrNull { it.idUser.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
        val newId = String.format("%s%06d", prefix, maxIdNum + 1)
        val now = LocalDateTime.now().toString()
        val role = if (isSeller) "SELLER" else "BUYER"
        val newUser = UserJson(
            idUser = newId, username = username.trim(), email = email.trim(),
            password = password, phone = phone.trim(), role = role,
            createAt = now, updateAt = now, imageURL = null
        )
        users.add(newUser)
        writeJson(userFile, users)
        if (isSeller) {
            val sellers = readJson<SellerJson>(sellerFile)
            val newSeller = SellerJson(
                idSeller = newId, nik = "", ktpPhoto = 0, bankName = "", accountNumber = ""
            )
            sellers.add(newSeller)
            writeJson(sellerFile, sellers)
        }
        RegisterResult.Success
    }
}