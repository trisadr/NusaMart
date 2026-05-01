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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.ui.theme.NusaMartTheme

// ─── Data Model ───────────────────────────────────────────────────────────────
data class PaymentMethod(
    val name: String,
    val icon: ImageVector,
    val methodKey: String,
    val subtitle: String? = null
)

data class PaymentSection(
    val header: String,
    val methods: List<PaymentMethod>
)

// ─── Data definisi metode pembayaran ─────────────────────────────────────────

private val paymentSections = listOf(
    PaymentSection(
        header = "Transfer Bank",
        methods = listOf(
            PaymentMethod("BCA", Icons.Default.AccountBalance, "Transfer Bank - BCA"),
            PaymentMethod("Mandiri", Icons.Default.AccountBalance, "Transfer Bank - Mandiri"),
            PaymentMethod("BNI", Icons.Default.AccountBalance, "Transfer Bank - BNI"),
            PaymentMethod("BRI", Icons.Default.AccountBalance, "Transfer Bank - BRI"),
        )
    ),
    PaymentSection(
        header = "E-Wallet & QRIS",
        methods = listOf(
            PaymentMethod(
                name = "QRIS (Gopay, OVO, Dana, LinkAja)",
                icon = Icons.Default.QrCodeScanner,
                methodKey = "QRIS"
            ),
            PaymentMethod("ShopeePay", Icons.Default.Wallet, "ShopeePay"),
            PaymentMethod("Dana", Icons.Default.Wallet, "Dana"),
            PaymentMethod("OVO", Icons.Default.Wallet, "OVO"),
        )
    ),
    PaymentSection(
        header = "Bayar di Tempat",
        methods = listOf(
            PaymentMethod(
                name = "COD (Bayar di Tempat)",
                icon = Icons.Default.Payments,
                methodKey = "COD",
                subtitle = "Bayar tunai ke kurir saat barang sampai"
            )
        )
    )
)

// ─── Screen ──────────────────────────────────────────────────────────────────

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
            backStack.removeAt(backStack.lastIndex)
            backStack.add(Routes.PaymentRoute(orderId, selectedMethod!!))
        }
    )
}

// ─── Content ─────────────────────────────────────────────────────────────────
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
            paymentSections.forEachIndexed { sectionIndex, section ->
                item(key = "header_${section.header}") {
                    if (sectionIndex > 0) Spacer(modifier = Modifier.height(4.dp))
                    PaymentSectionHeader(section.header)
                }

                items(
                    items = section.methods,
                    key = { method -> method.methodKey }
                ) { method ->
                    PaymentMethodItem(
                        name = method.name,
                        icon = method.icon,
                        selectedMethod = selectedMethod,
                        subtitle = method.subtitle
                    ) {
                        onMethodSelected(method.methodKey)
                    }
                }
            }
        }
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────

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