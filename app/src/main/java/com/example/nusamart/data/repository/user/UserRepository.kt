package com.example.nusamart.data.repository.user

import com.example.nusamart.data.dto.AddressDto
import com.example.nusamart.data.dto.UserProfileResponse
import com.example.nusamart.data.preference.TokenPrefs
import com.example.nusamart.data.repository.notif.NotificationRepository
import com.example.nusamart.feature.auth.login.AuthAndUserApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// --- Hasil Operasi ---
sealed class RegisterResult {
    object Success : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}

sealed class LoginResult {
    data class Success(val role: String, val userId: String, val token: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

@Singleton
class UserRepository @Inject constructor(
    private val api: AuthAndUserApi,
    private val tokenPrefs: TokenPrefs,
    private val notificationRepository: NotificationRepository
) {

    // =========================================================================
    // SESSION MANAGEMENT
    // =========================================================================

    suspend fun getActiveUserId(): String? {
        return tokenPrefs.getUserId().firstOrNull()
    }

    suspend fun getActiveUserRole(): String? {
        return tokenPrefs.getRole()
    }

    // =========================================================================
    // AUTHENTICATION
    // =========================================================================

    suspend fun login(emailOrUsername: String, password: String): LoginResult = withContext(Dispatchers.IO) {
        try {
            val request = mapOf(
                "emailOrUsername" to emailOrUsername.trim(),
                "password" to password.trim()
            )

            val response = api.login(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // UBAH: Tambahkan .user karena datanya dibungkus oleh Laravel
                    tokenPrefs.saveSession(
                        token = body.token,
                        role = body.user.role,
                        userId = body.user.idUser
                    )
                    return@withContext LoginResult.Success(body.user.role, body.user.idUser, body.token)
                }
            }
            return@withContext LoginResult.Error("Username/email atau password salah.")
        } catch (e: Exception) {
            return@withContext LoginResult.Error("Gagal terhubung ke server: ${e.message}")
        }
    }

    suspend fun register(
        username: String, email: String, phone: String, password: String, isSeller: Boolean,
        nik: String? = null, bankName: String? = null, accountNumber: String? = null
    ): RegisterResult = withContext(Dispatchers.IO) {
        try {
            val role = if (isSeller) "SELLER" else "BUYER"
            val request = mutableMapOf<String, Any>(
                "username" to username.trim(),
                "email" to email.trim(),
                "phone" to phone.trim(),
                "password" to password.trim(),
                "role" to role
            )

            if (isSeller) {
                request["nik"] = nik?.trim() ?: ""
                request["bankName"] = bankName?.trim() ?: ""
                request["accountNumber"] = accountNumber?.trim() ?: ""
            }

            val response = api.register(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    notificationRepository.addSystemNotification(
                        userId = body.user.idUser, // <-- PERBAIKAN DI SINI (tambahkan .user dan pastikan namanya idUser)
                        username = username.trim(),
                        isSeller = isSeller
                    )
                    return@withContext RegisterResult.Success
                }
            }
            return@withContext RegisterResult.Error("Registrasi gagal. Cek kembali data Anda.")
        } catch (e: Exception) {
            return@withContext RegisterResult.Error("Gagal terhubung ke server: ${e.message}")
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        try {
            api.logout()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            tokenPrefs.clearSession()
        }
    }

    // =========================================================================
    // PROFILE & ADDRESSES
    // =========================================================================

    suspend fun getCurrentProfile(): UserProfileResponse? = withContext(Dispatchers.IO) {
        try {
            val response = api.getProfile()
            if (response.isSuccessful) {
                return@withContext response.body()
            }
            return@withContext null
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    suspend fun getUserAddresses(): List<AddressDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAddresses()
            if (response.isSuccessful) {
                return@withContext response.body() ?: emptyList()
            }
            return@withContext emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }

    suspend fun addAddress(
        label: String, receiver: String, phone: String, completeAddress: String,
        city: String, province: String, postalCode: String, isDefault: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = mapOf(
                "label" to label,
                "receiver" to receiver,
                "phone" to phone,
                "completeAddress" to completeAddress,
                "city" to city,
                "province" to province,
                "postalCode" to postalCode,
                "isDefault" to isDefault
            )
            val response = api.addAddress(request)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    suspend fun updateAddress(
        addressId: String, label: String, receiver: String, phone: String,
        completeAddress: String, city: String, province: String, postalCode: String, isDefault: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = mapOf(
                "label" to label,
                "receiver" to receiver,
                "phone" to phone,
                "completeAddress" to completeAddress,
                "city" to city,
                "province" to province,
                "postalCode" to postalCode,
                "isDefault" to isDefault
            )
            val response = api.updateAddress(addressId, request)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    suspend fun deleteAddress(addressId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteAddress(addressId)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    suspend fun setDefaultAddress(addressId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.setDefaultAddress(addressId)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    suspend fun getUserById(userId: String): UserProfileResponse? = withContext(Dispatchers.IO) {
        try {
            val response = api.getUserById(userId)
            if (response.isSuccessful) {
                return@withContext response.body()
            }
            return@withContext null
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}