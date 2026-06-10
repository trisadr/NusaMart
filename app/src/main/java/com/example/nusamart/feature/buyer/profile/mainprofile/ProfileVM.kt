package com.example.nusamart.feature.buyer.profile.mainprofile

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
class ProfileVM @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        val currentUser = userRepository.getCurrentProfile()
        _uiState.update { it.copy(user = currentUser, isLoading = false) }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}