package com.example.nusamart.feature.buyer.profile.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.dto.AddressDto // UBAH IMPORT INI
import com.example.nusamart.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressVM @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

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
        _uiState.update { it.copy(isLoading = true) }
        val success = userRepository.deleteAddress(id)
        if (success) {
            loadAddresses()
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun showAddForm() = _uiState.update { it.copy(isFormVisible = true, editAddressId = null) }

    fun showEditForm(address: AddressDto) = _uiState.update {
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
            formPostalCode = "", formIsDefault = 0
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
    fun updateIsDefault(v: Int) = _uiState.update { it.copy(formIsDefault = v) }

    fun saveAddress() = viewModelScope.launch {
        val state = _uiState.value

        if (state.formLabel.isNotBlank() && state.formReceiver.isNotBlank() &&
            state.formPhone.isNotBlank() && state.formCompleteAddress.isNotBlank() &&
            state.formCity.isNotBlank() && state.formProvince.isNotBlank()) {

            _uiState.update { it.copy(isLoading = true) } // Loading saat memanggil API

            val isSuccess = if (state.editAddressId != null) {
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

            if (isSuccess) {
                hideForm()
                loadAddresses()
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}