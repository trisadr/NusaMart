package com.example.nusamart.feature.buyer.transaction.address

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
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
fun AddressOptionScreen(
    currentRoute: Routes.CheckoutRoute,
    vm: AddressOptionVM = hiltViewModel()
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Alamat Pengiriman") },
                navigationIcon = { IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (uiState.addresses.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Belum ada alamat tersimpan.") }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.addresses) { address ->
                    OptionItemCard(
                        name = "${address.label} - ${address.receiver}",
                        icon = Icons.Default.LocationOn,
                        isSelected = currentRoute.selectedAddressId == address.idAddress,
                        subtitle = "${address.phone}\n${address.completeAddress}, ${address.city}",
                        onSelect = {
                            backStack.removeAt(backStack.lastIndex) // Hapus layar ini
                            backStack.removeAt(backStack.lastIndex) // Hapus checkout lama
                            backStack.add(currentRoute.copy(selectedAddressId = address.idAddress)) // Tambah checkout baru
                        }
                    )
                }
            }
        }
    }
}