package com.example.nusamart.feature.landingpage

import androidx.navigation3.runtime.NavKey
import com.example.nusamart.core.Routes

data class LandingUiState(
    val isLoading: Boolean = true,
    val navigateTo: NavKey? = null
)