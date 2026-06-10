package com.example.nusamart.feature.landingpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.core.Routes
import com.example.nusamart.data.preference.TokenPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val tokenPrefs: TokenPrefs
) : ViewModel() {

    private val _uiState = MutableStateFlow(LandingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val isLoggedIn = tokenPrefs.isLoggedIn()

            if (isLoggedIn) {
                val role = tokenPrefs.getRole()
                val destination = when (role) {
                    "seller" -> Routes.SellerHomeScreenRoute
                    "buyer"  -> Routes.HomeRoute
                    else     -> null // role tidak dikenal, tetap di landing
                }
                _uiState.update {
                    it.copy(isLoading = false, navigateTo = destination)
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, navigateTo = null)
                }
            }
        }
    }

    fun onNavigated() {
        _uiState.update { it.copy(navigateTo = null) }
    }
}