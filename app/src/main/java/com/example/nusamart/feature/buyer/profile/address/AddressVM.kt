package com.example.nusamart.feature.buyer.profile.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.core.MyApplication
import com.example.nusamart.data.model.user.UserAddressJson
import com.example.nusamart.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddressVM(
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                AddressVM(userRepository = app.userRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(AddressUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAddresses()
    }

    private fun loadAddresses() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        val list = userRepository.getUserAddresses()
        _uiState.update { it.copy(addresses = list, isLoading = false) }
    }

    fun deleteAddress(id: String) = viewModelScope.launch {
        userRepository.deleteAddress(id)
        loadAddresses()
    }

    // --- Fungsi Form ---
    fun showAddForm() = _uiState.update { it.copy(isFormVisible = true, editAddressId = null) }

    // Membuka form dan mengisi dengan data alamat lama
    fun showEditForm(address: UserAddressJson) = _uiState.update {
        it.copy(
            isFormVisible = true,
            editAddressId = address.idAddress,
            formLabel = address.label,
            formReceiver = address.receiver,
            formPhone = address.phone,
            formCompleteAddress = address.completeAddress,
            formCity = address.city,
            formProvince = address.province,
            formPostalCode = address.postalCode,
            formIsDefault = address.isDefault
        )
    }

    fun hideForm() = _uiState.update {
        it.copy(
            isFormVisible = false,
            editAddressId = null,
            formLabel = "", formReceiver = "", formPhone = "",
            formCompleteAddress = "", formCity = "", formProvince = "",
            formPostalCode = "", formIsDefault = false
        )
    }

    fun updateLabel(v: String) = _uiState.update { it.copy(formLabel = v) }
    fun updateReceiver(v: String) = _uiState.update { it.copy(formReceiver = v) }
    fun updatePhone(v: String) {
        val filtered = v.filter { it.isDigit() }
        if (filtered.length <= 13) _uiState.update { it.copy(formPhone = filtered) }
    }
    fun updateCompleteAddress(v: String) = _uiState.update { it.copy(formCompleteAddress = v) }
    fun updateCity(v: String) = _uiState.update { it.copy(formCity = v) }
    fun updateProvince(v: String) = _uiState.update { it.copy(formProvince = v) }
    fun updatePostalCode(v: String) {
        val filtered = v.filter { it.isDigit() }
        if (filtered.length <= 5) _uiState.update { it.copy(formPostalCode = filtered) }
    }
    fun updateIsDefault(v: Boolean) = _uiState.update { it.copy(formIsDefault = v) }

    fun saveAddress() = viewModelScope.launch {
        val state = _uiState.value

        // Pastikan semua field wajib terisi
        if (state.formLabel.isNotBlank() && state.formReceiver.isNotBlank() &&
            state.formPhone.isNotBlank() && state.formCompleteAddress.isNotBlank() &&
            state.formCity.isNotBlank() && state.formProvince.isNotBlank()) {

            if (state.editAddressId != null) {
                // Proses Edit (Update)
                userRepository.updateAddress(
                    addressId = state.editAddressId,
                    label = state.formLabel,
                    receiver = state.formReceiver,
                    phone = state.formPhone,
                    completeAddress = state.formCompleteAddress,
                    city = state.formCity,
                    province = state.formProvince,
                    postalCode = state.formPostalCode,
                    isDefault = state.formIsDefault
                )
            } else {
                // Proses Tambah Baru
                userRepository.addAddress(
                    label = state.formLabel,
                    receiver = state.formReceiver,
                    phone = state.formPhone,
                    completeAddress = state.formCompleteAddress,
                    city = state.formCity,
                    province = state.formProvince,
                    postalCode = state.formPostalCode,
                    isDefault = state.formIsDefault
                )
            }

            hideForm()
            loadAddresses() // Refresh list setelah menyimpan
        }
    }
}