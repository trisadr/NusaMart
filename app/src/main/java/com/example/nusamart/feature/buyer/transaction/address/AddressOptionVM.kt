package com.example.nusamart.feature.buyer.transaction.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.core.MyApplication
import com.example.nusamart.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class AddressOptionVM(private val userRepository: UserRepository) : ViewModel() {
    companion object {
        val Factory = viewModelFactory { initializer { AddressOptionVM((this[APPLICATION_KEY] as MyApplication).userRepository) } }
    }
    private val _uiState = MutableStateFlow(AddressOptionUiState())
    val uiState = _uiState.asStateFlow()

    init { loadAddresses() }
    private fun loadAddresses() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        _uiState.update { it.copy(addresses = userRepository.getUserAddresses(), isLoading = false) }
    }
}