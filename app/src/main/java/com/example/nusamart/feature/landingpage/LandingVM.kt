package com.example.nusamart.feature.landingpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.core.Routes
import com.example.nusamart.data.preference.UserPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val userPreference: UserPreference
) : ViewModel() {

    private val _uiState = MutableStateFlow(LandingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            delay(1500L)
            val session = userPreference.getSession().first()
            val route = when {
                session.isLogin && session.role == "BUYER" -> Routes.HomeRoute
                else -> Routes.LoginPageRoute
            }
            _uiState.update {
                it.copy(isLoading = false, navigateTo = route)
            }
        }
    }

    fun onNavigated() {
        _uiState.update { it.copy(navigateTo = null) }
    }
}