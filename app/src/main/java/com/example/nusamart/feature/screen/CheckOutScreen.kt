package com.example.nusamart.feature.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data Models
data class CheckOutAddress(
    val recipientName: String,
    val phoneNumber: String,
    val fullAddress: String,
    val label: String,
)

data class CheckOutProduct(
    val id: String,
    val name: String,
    val pricePerItem: Long,
    val quantity: Int,
)

data class CheckOutShop(
    val shopName: String,
    val products: List<CheckOutProduct>,
    val shippingCourier: String,
    val shippingCost: Long,
    val estimatedArrival: String,
    val note: String,
)

data class CheckOutUiState(
    val address: CheckOutAddress,
    val shops: List<CheckOutShop>,
    val paymentMethod: String,
)

// Dummy Data
private val dummyUiState = CheckOutUiState(
    address = CheckOutAddress(
        recipientName = "Budi Santoso",
        phoneNumber = "(+62) 812-3456-7890",
        fullAddress = "Jl. Kenanga No. 12, RT 03/RW 05, Kel. Jaten, Kec. Karanganyar, Jawa Tengah 57772",
        label = "Rumah",
    ),
    shops = listOf(
        CheckOutShop(
            shopName = "TokoElektronik Official",
            products = listOf(
                CheckOutProduct("1", "Xiaomi Redmi Note 13 Pro 5G", 3_499_000L, 1),
//                CheckOutProduct("2", "Soft Case Anti Shock Premium Matte", 45_000L, 2),
            ),
            shippingCourier = "Cepet Pokoknya",
            shippingCost = 18_000L,
            estimatedArrival = "Tiba 19 - 21 Apr",
            note = "",
        )
//        CheckOutShop(
//            shopName = "FashionMu Store",
//            products = listOf(
//                CheckOutProduct("3", "Kaos Polos Oversize Unisex Cotton Combed 30s", 89_000L, 3),
//            ),
//            shippingCourier = "SiCepat",
//            shippingService = "GOKIL",
//            shippingCost = 12_000L,
//            estimatedArrival = "Tiba 18 - 19 Apr",
//            note = "",
//        ),
    ),
    paymentMethod = "Card?  Rp 1.500.000",
)

// Screen Entry Point
@Composable
fun CheckOutScreen(
    uiState: CheckOutUiState,
    onBackClick: () -> Unit,
    onPlaceOrderClick: () -> Unit,
) {
    Content(
        uiState = uiState,
        onBackClick = onBackClick,
        onPlaceOrderClick = onPlaceOrderClick,
    )
}

// Content
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: CheckOutUiState,
    onBackClick: () -> Unit,
    onPlaceOrderClick: () -> Unit,
) {
    val totalProductPrice = uiState.shops.sumOf { shop ->
        shop.products.sumOf { it.pricePerItem * it.quantity }
    }
    val totalShipping = uiState.shops.sumOf { it.shippingCost }
    val grandTotal = totalProductPrice + totalShipping
    val totalItems = uiState.shops.sumOf { shop -> shop.products.sumOf { it.quantity } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Checkout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        bottomBar = {
            OrderSummaryBottomBar(
                grandTotal = grandTotal,
                onPlaceOrderClick = onPlaceOrderClick,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item { ShippingAddressSection(address = uiState.address) }

            items(uiState.shops) { shop ->
                ShopOrderSection(shop = shop)
            }

            item { PaymentMethodSection(paymentMethod = uiState.paymentMethod) }

            item {
                PaymentDetailSection(
                    totalProductPrice = totalProductPrice,
                    totalShipping = totalShipping,
                    grandTotal = grandTotal,
                    totalItems = totalItems,
                )
            }
        }
    }
}

// Sections
@Composable
private fun ShippingAddressSection(address: CheckOutAddress) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "Alamat Pengiriman",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = address.recipientName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(4.dp),
                                )
                                .padding(horizontal = 6.dp, vertical = 1.dp),
                        ) {
                            Text(
                                text = address.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 10.sp,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = address.phoneNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = address.fullAddress,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                TextButton(onClick = {}) {
                    Text(text = "Ubah", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun ShopOrderSection(shop: CheckOutShop) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = shop.shopName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            shop.products.forEach { product -> ProductItem(product = product) }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Pengiriman
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = "Pengiriman",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "${shop.shippingCourier} •  ${shop.estimatedArrival}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Rp ${"%,d".format(shop.shippingCost).replace(',', '.')}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                TextButton(onClick = {}) {
                    Text(text = "Ganti", style = MaterialTheme.typography.bodySmall)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Catatan
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Catatan",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = shop.note.ifBlank { "Tambah catatan untuk penjual..." },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                )
                TextButton(onClick = {}) {
                    Text(
                        text = if (shop.note.isBlank()) "Tambah" else "Ubah",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductItem(product: CheckOutProduct) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Placeholder gambar produk
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Rp ${"%,d".format(product.pricePerItem).replace(',', '.')}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "x${product.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodSection(paymentMethod: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Metode Pembayaran",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = paymentMethod,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = ">",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PaymentDetailSection(
    totalProductPrice: Long,
    totalShipping: Long,
    grandTotal: Long,
    totalItems: Int,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Rincian Pembayaran",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
            PaymentRow(
                label = "Total Harga ($totalItems barang)",
                value = "Rp ${"%,d".format(totalProductPrice).replace(',', '.')}",
            )
            PaymentRow(
                label = "Total Ongkos Kirim",
                value = "Rp ${"%,d".format(totalShipping).replace(',', '.')}",
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            PaymentRow(
                label = "Total Pembayaran",
                value = "Rp ${"%,d".format(grandTotal).replace(',', '.')}",
                isTotal = true,
            )
        }
    }
}

@Composable
private fun PaymentRow(label: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface,
        )
    }
}

// Bottom Bar
@Composable
private fun OrderSummaryBottomBar(
    grandTotal: Long,
    onPlaceOrderClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Total Pembayaran",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "Rp ${"%,d".format(grandTotal).replace(',', '.')}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Button(
                onClick = onPlaceOrderClick,
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "Buat Pesanan",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// Preview
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CheckOutPreview() {
    MaterialTheme {
        CheckOutScreen(
            uiState = dummyUiState,
            onBackClick = {},
            onPlaceOrderClick = {},
        )
    }
}

// Preview loading
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CheckOutLoadingPreview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}