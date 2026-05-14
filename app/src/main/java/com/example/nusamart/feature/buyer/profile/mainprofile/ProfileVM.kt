package com.example.nusamart.feature.buyer.profile.mainprofile

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

class ProfileVM(
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                ProfileVM(userRepository = app.userRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        val currentUser = userRepository.getCurrentUser()
        _uiState.update { it.copy(user = currentUser, isLoading = false) }
    }

    fun logout() {
        userRepository.logout()
    }
}