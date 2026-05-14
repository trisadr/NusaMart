package com.example.nusamart.feature.buyer.profile.address

import com.example.nusamart.data.model.user.UserAddressJson

data class AddressUiState(
    val addresses: List<UserAddressJson> = emptyList(),
    val isLoading: Boolean = true,

    // State untuk Form Tambah/Edit Alamat
    val isFormVisible: Boolean = false,
    val editAddressId: String? = null, // Jika null berarti Tambah Baru. Jika ada isi, berarti Edit

    val formLabel: String = "",
    val formReceiver: String = "",
    val formPhone: String = "",
    val formCompleteAddress: String = "",
    val formCity: String = "",
    val formProvince: String = "",
    val formPostalCode: String = "",
    val formIsDefault: Boolean = false
)