package com.example.nusamart.feature.buyer.profile.address

import com.example.nusamart.data.dto.AddressDto // UBAH IMPORT INI

data class AddressUiState(
    val addresses: List<AddressDto> = emptyList(), // UBAH TIPE DATA
    val isLoading: Boolean = true,

    // State untuk Form Tambah/Edit Alamat
    val isFormVisible: Boolean = false,
    val editAddressId: String? = null,

    val formLabel: String = "",
    val formReceiver: String = "",
    val formPhone: String = "",
    val formCompleteAddress: String = "",
    val formCity: String = "",
    val formProvince: String = "",
    val formPostalCode: String = "",
    val formIsDefault: Boolean = false
)