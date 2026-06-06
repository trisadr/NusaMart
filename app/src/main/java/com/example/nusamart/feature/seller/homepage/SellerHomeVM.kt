package com.example.nusamart.feature.seller.homepage

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
class SellerHomeVM @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SellerHomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        // Ambil data user umum dan data spesifik penjual secara paralel/berurutan
        val currentUser = userRepository.getCurrentUser()
        val currentSeller = userRepository.getCurrentSeller()

        _uiState.update {
            it.copy(
                isLoading = false,
                user = currentUser,
                sellerInfo = currentSeller
            )
        }
    }

    // Fungsi logout jika diperlukan di dashboard
    fun logout() = viewModelScope.launch {
        userRepository.logout()
    }
}