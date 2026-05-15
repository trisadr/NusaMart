package com.example.nusamart.feature.buyer.transaction.courier

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.MyApplication
import com.example.nusamart.core.Routes
import com.example.nusamart.data.repository.shipping.CourierOptionJson
import com.example.nusamart.data.repository.shipping.ShippingRepository
import com.example.nusamart.feature.buyer.transaction.components.OptionItemCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierOptionScreen(
    currentRoute: Routes.CheckoutRoute,
    vm: CourierOptionVM = viewModel(factory = CourierOptionVM.Factory)
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Ekspedisi") },
                navigationIcon = { IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.couriers) { courier ->
                    OptionItemCard(
                        name = courier.courierName,
                        icon = Icons.Default.LocalShipping,
                        isSelected = currentRoute.selectedCourierId == courier.idCourier,
                        subtitle = "Estimasi: ${courier.timeEstimation}",
                        onSelect = {
                            backStack.removeAt(backStack.lastIndex)
                            backStack.removeAt(backStack.lastIndex)
                            backStack.add(currentRoute.copy(selectedCourierId = courier.idCourier))
                        }
                    )
                }
            }
        }
    }
}