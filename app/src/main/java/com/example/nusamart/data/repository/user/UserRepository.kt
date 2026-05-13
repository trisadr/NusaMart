package com.example.nusamart.data.repository.user

import com.example.nusamart.data.model.user.Seller
import com.example.nusamart.data.model.user.User
import com.example.nusamart.data.model.user.UserAddress
import java.time.LocalDateTime
import java.util.UUID

// Hasil operasi login — bisa sukses atau gagal dengan pesan error
sealed class AuthResult {
    data class Success(val user: User, val token: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class UserRepository(private val dataSource: UserLocalDataSource) {

    // ── Mapper: JSON → Data Class ─────────────────────────────────────────────
    private fun UserJson.toUser() = User(
        idUser = idUser,
        username = username,
        email = email,
        passwordHashed = passwordHashed,
        phone = phone,
        role = User.Role.valueOf(role),
        createAt = LocalDateTime.parse(createAt),
        updateAt = LocalDateTime.parse(updateAt),
        imageURL = imageURL
    )

    private fun User.toJson() = UserJson(
        idUser = idUser,
        username = username,
        email = email,
        passwordHashed = passwordHashed,
        phone = phone,
        role = role.name,
        createAt = createAt.toString(),
        updateAt = updateAt.toString(),
        imageURL = imageURL
    )

    private fun UserAddressJson.toAddress() = UserAddress(
        idAddress = idAddress,
        idUser = idUser,
        label = label,
        receiver = receiver,
        phone = phone,
        completeAddress = completeAddress,
        city = city,
        province = province,
        postalCode = postalCode,
        isDefault = isDefault
    )

    private fun UserAddress.toJson() = UserAddressJson(
        idAddress = idAddress,
        idUser = idUser,
        label = label,
        receiver = receiver,
        phone = phone,
        completeAddress = completeAddress,
        city = city,
        province = province,
        postalCode = postalCode,
        isDefault = isDefault
    )

    // ── AUTH ──────────────────────────────────────────────────────────────────

    fun login(email: String, password: String): AuthResult {
        val db = dataSource.readDatabase()

        // Cari user berdasarkan email
        val userJson = db.users.find { it.email == email }
            ?: return AuthResult.Error("Email tidak ditemukan")

        // Validasi password (di real app pakai bcrypt / hash check)
        // Sementara pakai simple check dulu
        if (userJson.passwordHashed != password) {
            return AuthResult.Error("Password salah")
        }

        // Buat session token baru
        val token = UUID.randomUUID().toString()
        val session = SessionJson(
            idUser = userJson.idUser,
            token = token,
            role = userJson.role,
            loginAt = LocalDateTime.now().toString()
        )

        // Hapus session lama untuk user ini, simpan yang baru
        db.sessions.removeAll { it.idUser == userJson.idUser }
        db.sessions.add(session)
        dataSource.writeDatabase(db)

        return AuthResult.Success(user = userJson.toUser(), token = token)
    }

    fun logout(userId: String) {
        val db = dataSource.readDatabase()
        db.sessions.removeAll { it.idUser == userId }
        dataSource.writeDatabase(db)
    }

    fun getActiveSession(): SessionJson? {
        val db = dataSource.readDatabase()
        return db.sessions.lastOrNull()
    }

    fun register(
        username: String,
        email: String,
        password: String,
        phone: String,
        role: User.Role = User.Role.BUYER
    ): AuthResult {
        val db = dataSource.readDatabase()

        // Cek email sudah dipakai
        if (db.users.any { it.email == email }) {
            return AuthResult.Error("Email sudah terdaftar")
        }

        // Cek username sudah dipakai
        if (db.users.any { it.username == username }) {
            return AuthResult.Error("Username sudah dipakai")
        }

        val now = LocalDateTime.now().toString()
        val newUser = UserJson(
            idUser = "usr-${UUID.randomUUID()}",
            username = username,
            email = email,
            passwordHashed = password,   // Di real app: hash dulu sebelum simpan
            phone = phone,
            role = role.name,
            createAt = now,
            updateAt = now
        )

        db.users.add(newUser)
        dataSource.writeDatabase(db)

        // Langsung login setelah register
        return login(email, password)
    }

    // ── USER ──────────────────────────────────────────────────────────────────

    fun getUserById(userId: String): User? {
        return dataSource.readDatabase().users
            .find { it.idUser == userId }
            ?.toUser()
    }

    fun updateUser(updatedUser: User): Boolean {
        val db = dataSource.readDatabase()
        val index = db.users.indexOfFirst { it.idUser == updatedUser.idUser }
        if (index == -1) return false

        db.users[index] = updatedUser.copy(updateAt = LocalDateTime.now()).toJson()
        dataSource.writeDatabase(db)
        return true
    }

    // ── SELLER ────────────────────────────────────────────────────────────────

    fun getSellerById(sellerId: String): Seller? {
        return dataSource.readDatabase().sellers
            .find { it.idSeller == sellerId }
            ?.let {
                Seller(
                    idSeller = it.idSeller,
                    nik = it.nik,
                    ktpPhoto = it.ktpPhoto,
                    bankName = it.bankName,
                    accountNumber = it.accountNumber
                )
            }
    }

    fun registerSeller(userId: String, nik: String, ktpPhoto: Int, bankName: String, accountNumber: String): Boolean {
        val db = dataSource.readDatabase()

        // Pastikan user ada dan rolenya SELLER
        val userIndex = db.users.indexOfFirst { it.idUser == userId }
        if (userIndex == -1) return false

        // Update role user jadi SELLER
        db.users[userIndex] = db.users[userIndex].copy(role = User.Role.SELLER.name)

        // Tambah data seller
        db.sellers.add(
            SellerJson(
                idSeller = userId,
                nik = nik,
                ktpPhoto = ktpPhoto,
                bankName = bankName,
                accountNumber = accountNumber
            )
        )

        dataSource.writeDatabase(db)
        return true
    }

    // ── USER ADDRESS ──────────────────────────────────────────────────────────

    fun getAddressesByUser(userId: String): List<UserAddress> {
        return dataSource.readDatabase().addresses
            .filter { it.idUser == userId }
            .map { it.toAddress() }
    }

    fun getDefaultAddress(userId: String): UserAddress? {
        return dataSource.readDatabase().addresses
            .find { it.idUser == userId && it.isDefault }
            ?.toAddress()
    }

    fun addAddress(address: UserAddress): Boolean {
        val db = dataSource.readDatabase()

        // Kalau isDefault true, reset semua address lain milik user ini
        if (address.isDefault) {
            val indices = db.addresses.indices.filter { db.addresses[it].idUser == address.idUser }
            indices.forEach { db.addresses[it] = db.addresses[it].copy(isDefault = false) }
        }

        db.addresses.add(address.toJson())
        dataSource.writeDatabase(db)
        return true
    }

    fun updateAddress(updatedAddress: UserAddress): Boolean {
        val db = dataSource.readDatabase()
        val index = db.addresses.indexOfFirst { it.idAddress == updatedAddress.idAddress }
        if (index == -1) return false

        // Kalau dijadikan default, reset yang lain
        if (updatedAddress.isDefault) {
            val indices = db.addresses.indices.filter {
                db.addresses[it].idUser == updatedAddress.idUser && it != index
            }
            indices.forEach { db.addresses[it] = db.addresses[it].copy(isDefault = false) }
        }

        db.addresses[index] = updatedAddress.toJson()
        dataSource.writeDatabase(db)
        return true
    }

    fun deleteAddress(addressId: String): Boolean {
        val db = dataSource.readDatabase()
        val removed = db.addresses.removeAll { it.idAddress == addressId }
        if (removed) dataSource.writeDatabase(db)
        return removed
    }
}