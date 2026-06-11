package com.example.nusamart.feature.buyer.transaction.courier

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.feature.buyer.transaction.components.OptionItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierOptionScreen(
    currentRoute: Routes.CheckoutRoute,
    vm: CourierOptionVM = hiltViewModel()
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
                        subtitle = "Estimasi: ${courier.timeEstimation} Hari",
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