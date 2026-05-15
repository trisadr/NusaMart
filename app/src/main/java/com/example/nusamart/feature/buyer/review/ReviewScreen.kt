package com.example.nusamart.feature.buyer.review

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nusamart.R
import com.example.nusamart.core.LocalBackStack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    orderId: String,
    vm: ReviewVM = viewModel(factory = ReviewVM.Factory)
) {
    val context = LocalContext.current
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(orderId) {
        vm.loadOrderItems(orderId)
    }

    LaunchedEffect(uiState.isSubmitSuccess) {
        if (uiState.isSubmitSuccess) {
            Toast.makeText(context, "Ulasan berhasil dikirim!", Toast.LENGTH_SHORT).show()
            backStack.removeAt(backStack.lastIndex)
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFFF6D00))
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tulis Ulasan", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            // tombol tidak muncul jika pesanan belum selesai atau semua barang sudah diulas
            if (uiState.isOrderDelivered && !uiState.allReviewed) {
                Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {
                        if (uiState.showValidationError) {
                            Text(
                                text = "Harap beri bintang untuk semua produk",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        Button(
                            onClick = vm::submitReviews,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00))
                        ) {
                            Text("Kirim Ulasan", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            if (!uiState.isOrderDelivered) {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Pesanan Belum Selesai",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Kamu hanya bisa memberikan ulasan setelah pesanan berstatus Selesai (Diterima).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            else if (uiState.allReviewed) {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color(0xFF4CAF50))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Semua Sudah Diulas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Terima kasih! Kamu sudah memberikan ulasan untuk semua produk di pesanan ini.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            else {
                uiState.itemsToReview.forEachIndexed { index, form ->
                    ReviewItemSection(
                        form = form,
                        onRatingChange = { rating -> vm.updateRating(form.idOrderItem, rating) },
                        onReviewTextChange = { text -> vm.updateComment(form.idOrderItem, text) }
                    )

                    if (index < uiState.itemsToReview.lastIndex) {
                        HorizontalDivider(thickness = 8.dp, color = MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewItemSection(
    form: ReviewItemForm,
    onRatingChange: (Int) -> Unit,
    onReviewTextChange: (String) -> Unit
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // Info Barang
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            val safeImageResId = remember(form.productImageResId) {
                try {
                    context.resources.getResourceName(form.productImageResId)
                    form.productImageResId
                } catch (e: Exception) { R.drawable.nm_logo }
            }

            Image(
                painter = painterResource(id = safeImageResId),
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = form.productName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "${form.quantity} barang", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Input Rating
        Text("Bagaimana kualitas produk ini?", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.Start) {
            repeat(5) { index ->
                val starValue = index + 1
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Bintang $starValue",
                    tint = if (starValue <= form.rating) Color(0xFFFFC107) else Color.LightGray,
                    modifier = Modifier.size(40.dp).padding(4.dp).clickable { onRatingChange(starValue) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Input Foto
        Text("Tambahkan Foto (Opsional)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { Toast.makeText(context, "Fitur upload foto segera hadir!", Toast.LENGTH_SHORT).show() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color(0xFFFF6D00), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tambah", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFF6D00))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Input Teks
        Text("Tulis Ulasan (Opsional)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = form.comment,
            onValueChange = onReviewTextChange,
            modifier = Modifier.fillMaxWidth().height(120.dp),
            placeholder = { Text("Ceritakan pengalamanmu menggunakan produk ini...", color = Color.Gray) },
            shape = RoundedCornerShape(12.dp)
        )
    }
}