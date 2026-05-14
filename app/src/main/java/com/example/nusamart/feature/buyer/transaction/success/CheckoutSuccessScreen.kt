package com.example.nusamart.feature.buyer.transaction.success

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.R
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.MyApplication
import com.example.nusamart.core.Routes
import com.example.nusamart.data.repository.transaction.TransactionRepository
import com.example.nusamart.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch





// --- SCREEN ---
@Composable
fun CheckoutSuccessScreen(
    paymentId: String,
    orderId: String,
    vm: CheckoutSuccessVM = viewModel(factory = CheckoutSuccessVM.Factory)
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    // Pass orderId juga agar VM bisa menarik data real dari database
    LaunchedEffect(paymentId, orderId) { vm.loadData(paymentId, orderId) }

    Scaffold(
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = { backStack.clear(); backStack.add(Routes.HomeRoute) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding(),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Kembali ke Beranda") }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(80.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text("Pesanan Berhasil Dibuat!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))

            val instruksi = if (uiState.provider == "COD") {
                "Silakan siapkan uang tunai sejumlah total tagihan saat kurir datang."
            } else {
                "Selesaikan pembayaran sebelum batas waktu agar pesanan diproses."
            }
            Text(instruksi, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(32.dp))

            // --- LOGIKA TAMPILAN BERDASARKAN PROVIDER ---
            when (uiState.provider) {
                "MIDTRANS" -> {
                    // Tampilan QRIS
                    Card(
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Scan QRIS Berikut", style = MaterialTheme.typography.labelLarge)
                            Spacer(modifier = Modifier.height(16.dp))
                            // GANTI logo di bawah ini dengan gambar QRIS-mu jika sudah ada (misal: R.drawable.img_qris)
                            Image(
                                painter = painterResource(id = R.drawable.nm_logo),
                                contentDescription = "QRIS Code",
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                "MANUAL" -> {
                    // Tampilan Transfer Bank
                    Card(
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Kode Pembayaran ${uiState.bankName}", style = MaterialTheme.typography.labelLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(uiState.paymentCode, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, letterSpacing = 2.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                "COD" -> {
                    // Jika COD, tidak merender Card apapun (Kosong)
                }
            }

            // ID Pesanan tetap ditampilkan untuk semua metode
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ID Pesanan", color = Color.Gray)
                    Text(orderId, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(
                onClick = { backStack.add(Routes.OrderDetailRoute(orderId)) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)
            ) { Text("Lihat Pesanan") }
        }
    }
}