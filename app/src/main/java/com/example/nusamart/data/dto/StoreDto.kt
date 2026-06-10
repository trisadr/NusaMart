package com.example.nusamart.data.dto

import com.google.gson.annotations.SerializedName

data class StoreDto(
    @SerializedName("idStore") val idStore: String,
    @SerializedName("idSeller") val idSeller: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("logoURL") val logoURL: String?,
    @SerializedName("location") val location: String,
    @SerializedName("urlLocation") val urlLocation: String?,
    @SerializedName("createAt") val createAt: String?,
    @SerializedName("updateAt") val updateAt: String?
)

data class UpdateStoreResponse(
    @SerializedName("message") val message: String,
    @SerializedName("store") val store: StoreDto
)