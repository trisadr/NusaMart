package com.example.nusamart.feature.buyer.transaction.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import com.example.nusamart.data.repository.order.OrderItemInput
import com.example.nusamart.data.repository.order.OrderRepository
import com.example.nusamart.data.repository.product.ProductRepository
import com.example.nusamart.data.repository.shipping.ShippingRepository
import com.example.nusamart.data.repository.transaction.TransactionRepository
import com.example.nusamart.data.repository.user.UserRepository
import com.example.nusamart.feature.buyer.transaction.components.DetailRow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(route: Routes.CheckoutRoute, vm: CheckoutVM = viewModel(factory = CheckoutVM.Factory)) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(route) { vm.loadData(route) }

    val total = uiState.subtotal + uiState.shippingCost + uiState.serviceFee
    val isReadyToCheckout = uiState.address != null && route.selectedCourierId != null && route.selectedPaymentMethodId != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = { IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Tagihan", style = MaterialTheme.typography.labelLarge)
                        Text("Rp ${total.toLong()}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            vm.placeOrder(route) { paymentId, orderId ->
                                backStack.removeAt(backStack.lastIndex)
                                backStack.add(Routes.CheckoutSuccessRoute(paymentId, orderId))
                            }
                        },
                        enabled = isReadyToCheckout && !uiState.isLoading,
                        modifier = Modifier.width(180.dp), shape = RoundedCornerShape(10.dp)
                    ) { Text("Buat Pesanan") }
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).background(MaterialTheme.colorScheme.surface),
                contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Alamat
                item {
                    Text("Alamat Pengiriman", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedCard(onClick = { backStack.add(Routes.AddressOptionRoute(route)) }, modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            if (uiState.address != null) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${uiState.address!!.receiver} | ${uiState.address!!.phone}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text("${uiState.address!!.completeAddress}, ${uiState.address!!.city}", style = MaterialTheme.typography.bodySmall)
                                }
                            } else {
                                Text("Pilih Alamat Pengiriman", color = Color.Gray, modifier = Modifier.weight(1f))
                            }
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                        }
                    }
                }

                // Produk
                item {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Pesanan Kamu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    uiState.items.forEach { item ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.nameSnapshot, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("${item.quantity} barang", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Text("Rp ${(item.priceSnapshot * item.quantity).toLong()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Kurir
                item {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Opsi Pengiriman", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedCard(modifier = Modifier.fillMaxWidth(), onClick = { backStack.add(Routes.CourierOptionRoute(route)) }) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalShipping, null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(uiState.courierName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                        }
                    }
                }

                // Pembayaran
                item {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Metode Pembayaran", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedCard(modifier = Modifier.fillMaxWidth(), onClick = { backStack.add(Routes.PaymentOptionRoute(route)) }) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountBalanceWallet, null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(uiState.paymentName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                        }
                    }
                }

                // Rincian
                item {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Rincian Pembayaran", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow("Subtotal Produk", "Rp ${uiState.subtotal.toLong()}")
                    DetailRow("Subtotal Pengiriman", "Rp ${uiState.shippingCost.toLong()}")
                    DetailRow("Biaya Layanan", "Rp ${uiState.serviceFee.toLong()}")
                }
            }
        }

        // Dialog Alamat
        if (uiState.showAddressDialog) {
            AlertDialog(
                onDismissRequest = vm::dismissDialog,
                title = { Text("Alamat Pengiriman Kosong") },
                text = { Text("Silakan pilih atau tambahkan alamat pengiriman terlebih dahulu sebelum membuat pesanan.") },
                confirmButton = { TextButton(onClick = { vm.dismissDialog(); backStack.add(Routes.AddressListRoute) }) { Text("Atur Alamat") } },
                dismissButton = { TextButton(onClick = vm::dismissDialog) { Text("Tutup") } }
            )
        }
    }
}