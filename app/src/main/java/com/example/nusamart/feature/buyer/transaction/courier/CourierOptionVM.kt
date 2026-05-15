package com.example.nusamart.feature.buyer.transaction.courier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.core.MyApplication
import com.example.nusamart.data.repository.shipping.CourierOptionJson
import com.example.nusamart.data.repository.shipping.ShippingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CourierOptionVM(private val shippingRepository: ShippingRepository) : ViewModel() {
    companion object {
        val Factory = viewModelFactory { initializer { CourierOptionVM((this[APPLICATION_KEY] as MyApplication).shippingRepository) } }
    }
    private val _uiState = MutableStateFlow(CourierOptionUiState())
    val uiState = _uiState.asStateFlow()

    init { loadCouriers() }
    private fun loadCouriers() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        var list = shippingRepository.getActiveCouriers()
        // Dummy data sementara kalau json dan file repository belum ada
        if (list.isEmpty()) {
            list = listOf(
                CourierOptionJson("CUR-001", "JNE Reguler", "REGULAR", "2-3 Hari", true),
                CourierOptionJson("CUR-002", "SiCepat HALU", "REGULAR", "1-2 Hari", true),
                CourierOptionJson("CUR-003", "J&T Jemari", "KARGO", "5-7 Hari", true)
            )
        }
        _uiState.update { it.copy(couriers = list, isLoading = false) }
    }
}