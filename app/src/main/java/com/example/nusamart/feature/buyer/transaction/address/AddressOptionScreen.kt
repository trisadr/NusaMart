package com.example.nusamart.feature.buyer.transaction.address

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
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
import com.example.nusamart.data.model.user.UserAddressJson
import com.example.nusamart.data.repository.user.UserRepository
import com.example.nusamart.feature.buyer.transaction.components.OptionItemCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressOptionScreen(
    currentRoute: Routes.CheckoutRoute,
    vm: AddressOptionVM = viewModel(factory = AddressOptionVM.Factory)
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