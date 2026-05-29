package com.example.nusamart.feature.buyer.transaction.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressOptionVM @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddressOptionUiState())
    val uiState = _uiState.asStateFlow()

    init { loadAddresses() }
    private fun loadAddresses() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        _uiState.update { it.copy(addresses = userRepository.getUserAddresses(), isLoading = false) }
    }
}