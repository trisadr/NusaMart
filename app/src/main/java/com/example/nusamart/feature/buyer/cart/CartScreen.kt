package com.example.nusamart.feature.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nusamart.ui.theme.NusaMartTheme

// Data model (dummy)
data class CartItemUiModel(
    val id: String,
    val shopName: String,
    val productName: String,
    val priceFormatted: String,
    val quantity: Int,
    val isChecked: Boolean,
)

private val dummyCartItems = listOf(
    CartItemUiModel(
        id = "1",
        shopName = "Toko Elektronik Jaya",
        productName = "Headphone Bluetooth Nirkabel Premium Bass Booster",
        priceFormatted = "Rp149.000",
        quantity = 1,
        isChecked = true,
    ),
    CartItemUiModel(
        id = "2",
        shopName = "Toko Elektronik Jaya",
        productName = "Kabel USB-C Fast Charging 65W 1 Meter",
        priceFormatted = "Rp35.000",
        quantity = 2,
        isChecked = true,
    ),
    CartItemUiModel(
        id = "3",
        shopName = "Fashion Hits Store",
        productName = "Kaos Polos Oversize Unisex Cotton Combed 30s",
        priceFormatted = "Rp89.000",
        quantity = 1,
        isChecked = false,
    ),
    CartItemUiModel(
        id = "4",
        shopName = "Dapur Sehat Official",
        productName = "Tumbler Stainless Steel 500ml Anti Bocor Vacuum",
        priceFormatted = "Rp75.000",
        quantity = 3,
        isChecked = true,
    ),
)

// Screen entry point
@Composable
fun CartScreen(
    onBackClick: () -> Unit = {}
) {
    Content(onBackClick = onBackClick)
}

// Content (stateless)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(
    items: List<CartItemUiModel> = dummyCartItems,
    isAllChecked: Boolean = false,
    totalPrice: String = "Rp348.000",
    totalItems: Int = 3,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CartTopBar(
                itemCount = items.size,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            CartBottomBar(
                isAllChecked = isAllChecked,
                totalPrice = totalPrice,
                totalItems = totalItems,
            )
        },
    ) { innerPadding ->
        if (items.isEmpty()) {
            CartEmptyState(modifier = Modifier.padding(innerPadding))
        } else {
            CartItemList(
                items = items,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

// Top bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartTopBar(
    itemCount: Int,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Keranjang ($itemCount)",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                )
            }
        },
        actions = {
            TextButton(onClick = {}) {
                Text(text = "Kelola", color = MaterialTheme.colorScheme.primary)
            }
        },
    )
}

// List of cart items (grouped by shop)
@Composable
private fun CartItemList(
    items: List<CartItemUiModel>,
    modifier: Modifier = Modifier,
) {
    val grouped = items.groupBy { it.shopName }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        grouped.forEach { (shopName, shopItems) ->
            item(key = shopName) {
                ShopGroup(
                    shopName = shopName,
                    items = shopItems,
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// Shop group card
@Composable
private fun ShopGroup(
    shopName: String,
    items: List<CartItemUiModel>,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            ShopHeaderRow(shopName = shopName)

            HorizontalDivider(thickness = 0.5.dp)

            items.forEachIndexed { index, item ->
                CartItemRow(item = item)
                if (index < items.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun ShopHeaderRow(shopName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Checkbox(
            checked = false,
            onCheckedChange = {}
        )

        Text(
            text = shopName,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CartItemRow(item: CartItemUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = {},
            modifier = Modifier.padding(top = 2.dp)
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = item.productName,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp,
            )

            Text(
                text = item.priceFormatted,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                QuantityStepper(quantity = item.quantity)

                IconButton(
                    onClick = {},
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

// Quantity stepper
@Composable
private fun QuantityStepper(quantity: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedIconButton(
            onClick = {},
            modifier = Modifier.size(28.dp),
            shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp),
        ) {
            Text(text = "−", fontSize = 14.sp)
        }

        Box(
            modifier = Modifier
                .width(36.dp)
                .height(28.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = quantity.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        OutlinedIconButton(
            onClick = {},
            modifier = Modifier.size(28.dp),
            shape = RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp),
        ) {
            Text(text = "+", fontSize = 14.sp)
        }
    }
}

// Empty state
@Composable
private fun CartEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = "🛒", fontSize = 64.sp)
            Text(
                text = "Keranjangmu masih kosong",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
            Text(
                text = "Yuk, mulai belanja sekarang!",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = {}) {
                Text(text = "Mulai Belanja")
            }
        }
    }
}

// Bottom bar
@Composable
private fun CartBottomBar(
    isAllChecked: Boolean,
    totalPrice: String,
    totalItems: Int,
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Checkbox(
                    checked = isAllChecked,
                    onCheckedChange = {}
                )
                Text(
                    text = "Semua",
                    fontSize = 13.sp,
                )
            }

            Text(
                text = totalPrice,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = {},
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Beli ($totalItems)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

// Previews utama
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CartPreview() {
    NusaMartTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Content(
                items = dummyCartItems,
                isAllChecked = false,
                totalPrice = "Rp348.000",
                totalItems = 3,
                onBackClick = {}
            )
        }
    }
}

// Preview kondisi Cart kosong
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CartEmptyPreview() {
    NusaMartTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Content(
                items = emptyList(),
                isAllChecked = false,
                totalPrice = "Rp0",
                totalItems = 0,
                onBackClick = {}
            )
        }
    }
}

// Preview kondisi loading
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CartLoadingPreview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
