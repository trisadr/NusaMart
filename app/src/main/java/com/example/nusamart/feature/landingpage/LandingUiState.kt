package com.example.nusamart.feature.landingpage

import androidx.navigation3.runtime.NavKey

data class LandingUiState(
    val isLoading: Boolean = true,
    val navigateTo: NavKey? = null
)