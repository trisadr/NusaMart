package com.example.nusamart.feature.buyer.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.ui.theme.NusaMartTheme

// ==========================================
// STATEFUL SCREEN
// ==========================================

@Composable
fun PaymentConfirmationScreen(orderId: String) {
    val backStack = LocalBackStack.current
    var selectedMethod by remember { mutableStateOf<String?>(null) }

    Content(
        selectedMethod = selectedMethod,
        onMethodSelected = { selectedMethod = it },

        onBackClick = {
            if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex)
        },

        onConfirm = {
            if (selectedMethod == null) return@Content

            // Pop PaymentConfirmationScreen, push PaymentScreen baru dengan metode terpilih
            backStack.removeAt(backStack.lastIndex)
            backStack.add(Routes.PaymentRoute(orderId, selectedMethod!!))
        }
    )
}

// ==========================================
// STATELESS CONTENT
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    selectedMethod: String?,
    onMethodSelected: (String) -> Unit,
    onBackClick: () -> Unit,
    onConfirm: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Pilih Metode Pembayaran",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    enabled = selectedMethod != null,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Konfirmasi Metode")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { PaymentSectionHeader("Transfer Bank") }
            item {
                PaymentMethodItem("BCA", Icons.Default.AccountBalance, selectedMethod) {
                    onMethodSelected("Transfer Bank - BCA")
                }
            }
            item {
                PaymentMethodItem("Mandiri", Icons.Default.AccountBalance, selectedMethod) {
                    onMethodSelected("Transfer Bank - Mandiri")
                }
            }
            item {
                PaymentMethodItem("BNI", Icons.Default.AccountBalance, selectedMethod) {
                    onMethodSelected("Transfer Bank - BNI")
                }
            }
            item {
                PaymentMethodItem("BRI", Icons.Default.AccountBalance, selectedMethod) {
                    onMethodSelected("Transfer Bank - BRI")
                }
            }

            item { Spacer(modifier = Modifier.height(4.dp)) }
            item { PaymentSectionHeader("E-Wallet & QRIS") }
            item {
                PaymentMethodItem(
                    "QRIS (Gopay, OVO, Dana, LinkAja)",
                    Icons.Default.QrCodeScanner,
                    selectedMethod
                ) { onMethodSelected("QRIS") }
            }
            item {
                PaymentMethodItem("ShopeePay", Icons.Default.Wallet, selectedMethod) {
                    onMethodSelected("ShopeePay")
                }
            }
            item {
                PaymentMethodItem("Dana", Icons.Default.Wallet, selectedMethod) {
                    onMethodSelected("Dana")
                }
            }
            item {
                PaymentMethodItem("OVO", Icons.Default.Wallet, selectedMethod) {
                    onMethodSelected("OVO")
                }
            }

            item { Spacer(modifier = Modifier.height(4.dp)) }
            item { PaymentSectionHeader("Bayar di Tempat") }
            item {
                PaymentMethodItem(
                    name = "COD (Bayar di Tempat)",
                    icon = Icons.Default.Payments,
                    selectedMethod = selectedMethod,
                    subtitle = "Bayar tunai ke kurir saat barang sampai"
                ) { onMethodSelected("COD") }
            }
        }
    }
}

// ==========================================
// PREVIEW
// ==========================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PaymentConfirmationScreenPreview() {
    NusaMartTheme(dynamicColor = false) {
        Content(
            selectedMethod = "Transfer Bank - BCA",
            onMethodSelected = {},
            onBackClick = {},
            onConfirm = {}
        )
    }
}