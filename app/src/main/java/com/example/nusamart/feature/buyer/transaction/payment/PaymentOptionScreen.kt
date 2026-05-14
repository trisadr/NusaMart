package com.example.nusamart.feature.buyer.transaction.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCodeScanner
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.feature.buyer.transaction.components.OptionItemCard
import com.example.nusamart.feature.buyer.transaction.components.PaymentSectionHeader

// --- SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentOptionScreen(
    currentRoute: Routes.CheckoutRoute,
    vm: PaymentOptionVM = viewModel(factory = PaymentOptionVM.Factory)
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    val qrisMethods = uiState.methods.filter { it.provider == "MIDTRANS" }
    val bankMethods = uiState.methods.filter { it.provider == "MANUAL" }
    val codMethods = uiState.methods.filter { it.provider == "COD" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Metode Pembayaran") },
                navigationIcon = { IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

                if (qrisMethods.isNotEmpty()) {
                    item { PaymentSectionHeader("E-Wallet & QRIS") }
                    items(qrisMethods) { method ->
                        OptionItemCard(
                            name = method.methodName, icon = Icons.Default.QrCodeScanner,
                            isSelected = currentRoute.selectedPaymentMethodId == method.idMethod,
                            subtitle = "Bayar otomatis pakai E-Wallet",
                            onSelect = { selectAndReturn(backStack, currentRoute, method.idMethod) }
                        )
                    }
                }

                if (bankMethods.isNotEmpty()) {
                    item { Spacer(modifier = Modifier.height(8.dp)); PaymentSectionHeader("Transfer Bank Manual") }
                    items(bankMethods) { method ->
                        OptionItemCard(
                            name = method.methodName, icon = Icons.Default.AccountBalance,
                            isSelected = currentRoute.selectedPaymentMethodId == method.idMethod,
                            onSelect = { selectAndReturn(backStack, currentRoute, method.idMethod) }
                        )
                    }
                }

                if (codMethods.isNotEmpty()) {
                    item { Spacer(modifier = Modifier.height(8.dp)); PaymentSectionHeader("Bayar di Tempat") }
                    items(codMethods) { method ->
                        OptionItemCard(
                            name = method.methodName, icon = Icons.Default.Payments,
                            isSelected = currentRoute.selectedPaymentMethodId == method.idMethod,
                            subtitle = "Bayar tunai ke kurir saat pesanan tiba",
                            onSelect = { selectAndReturn(backStack, currentRoute, method.idMethod) }
                        )
                    }
                }
            }
        }
    }
}

private fun selectAndReturn(backStack: NavBackStack<NavKey>, route: Routes.CheckoutRoute, methodId: String) {
    backStack.removeAt(backStack.lastIndex)
    backStack.removeAt(backStack.lastIndex)
    backStack.add(route.copy(selectedPaymentMethodId = methodId))
}