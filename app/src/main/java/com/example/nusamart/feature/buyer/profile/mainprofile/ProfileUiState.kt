package com.example.nusamart.feature.buyer.profile.mainprofile

import com.example.nusamart.data.dto.UserProfileResponse

data class ProfileUiState(
    val user: UserProfileResponse? = null,
    val isLoading: Boolean = true
)