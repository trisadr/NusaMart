package com.example.nusamart.data.dto

import com.google.gson.annotations.SerializedName

// Response
data class AuthResponse(
    @SerializedName("message") val message: String,
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserDto
)

data class UserDto(
    @SerializedName("idUser") val idUser: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String
)

// Response untuk GET /api/user/profile
data class UserProfileResponse(
    @SerializedName("idUser") val idUser: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("role") val role: String,
    @SerializedName("imageURL") val imageURL: String?,
    @SerializedName("createAt") val createAt: String?,
    @SerializedName("seller") val seller: SellerDto?
)

data class SellerDto(
    @SerializedName("nik") val nik: String,
    @SerializedName("bankName") val bankName: String,
    @SerializedName("accountNumber") val accountNumber: String
)

// Response & Request untuk Alamat
data class AddressDto(
    @SerializedName("idAddress") val idAddress: String,
    @SerializedName("idUser") val idUser: String,
    @SerializedName("label") val label: String,
    @SerializedName("receiver") val receiver: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("completeAddress") val completeAddress: String,
    @SerializedName("city") val city: String,
    @SerializedName("province") val province: String,
    @SerializedName("postalCode") val postalCode: String,
    @SerializedName("isDefault") val isDefault: Int
)

data class CommonResponse<T>(
    @SerializedName("message") val message: String,
    @SerializedName("address") val address: T? = null,
    @SerializedName("user") val user: T? = null
)