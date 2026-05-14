package com.example.nusamart.feature.buyer.profile.mainprofile

import com.example.nusamart.data.model.user.UserJson

data class ProfileUiState(
    val user: UserJson? = null,
    val isLoading: Boolean = true
)