package com.example.nusamart.feature.buyer.transaction.courier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.repository.shipping.CourierOptionJson
import com.example.nusamart.data.repository.shipping.ShippingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourierOptionVM @Inject constructor(
    private val shippingRepository: ShippingRepository,
) : ViewModel() {
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