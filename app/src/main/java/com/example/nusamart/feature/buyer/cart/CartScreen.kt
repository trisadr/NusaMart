package com.example.nusamart.feature.buyer.cart

import androidx.compose.foundation.Image
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.feature.components.BottomMenu
import com.example.nusamart.feature.components.NusaMartBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(vm: CartVM = viewModel(factory = CartVM.Factory)) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Keranjang", fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                if (uiState.storeGroups.isNotEmpty() && !uiState.isLoading) {
                    CartBottomBar(
                        isAllChecked = uiState.isAllChecked,
                        totalPrice = uiState.totalPrice,
                        checkedCount = uiState.checkedCount,
                        onAllCheckedChange = vm::toggleAllChecked,
                        onCheckout = {
                            if (uiState.checkedCount > 0) {
                                backStack.add(Routes.CheckoutRoute(fromCart = true))
                            }
                        }
                    )
                }
                NusaMartBottomNavigation(
                    selectedMenu = BottomMenu.CART,
                    onMenuSelected = { menu ->
                        when (menu) {
                            BottomMenu.HOME -> backStack.add(Routes.HomeRoute)
                            BottomMenu.NOTIFICATION -> backStack.add(Routes.NotificationRoute)
                            BottomMenu.CART -> Unit
                            BottomMenu.PROFILE -> backStack.add(Routes.ProfileRoute)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.storeGroups.isEmpty()) {
            CartEmptyState(
                modifier = Modifier.padding(innerPadding),
                onShopClick = { backStack.add(Routes.HomeRoute) }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    count = uiState.storeGroups.size,
                    key = { index -> uiState.storeGroups[index].storeId }
                ) { index ->
                    ShopGroup(
                        group = uiState.storeGroups[index],
                        onCheckedChange = vm::toggleChecked,
                        onQuantityIncrease = vm::increaseQuantity,
                        onQuantityDecrease = vm::decreaseQuantity,
                        onDeleteItem = vm::deleteItem
                    )
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun ShopGroup(
    group: StoreCartGroup,
    onCheckedChange: (String, Boolean) -> Unit,
    onQuantityIncrease: (String, Int) -> Unit,
    onQuantityDecrease: (String, Int) -> Unit,
    onDeleteItem: (String) -> Unit
) {
    val allChecked = group.items.all { it.isChecked }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = allChecked,
                    onCheckedChange = { checked -> group.items.forEach { onCheckedChange(it.idCartItem, checked) } }
                )
                Text(
                    text = group.storeName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                    modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }

            HorizontalDivider(thickness = 0.5.dp)

            group.items.forEachIndexed { index, item ->
                CartItemRow(
                    item = item,
                    onCheckedChange = { checked -> onCheckedChange(item.idCartItem, checked) },
                    onIncrease = { onQuantityIncrease(item.idCartItem, item.quantity) },
                    onDecrease = { onQuantityDecrease(item.idCartItem, item.quantity) },
                    onDelete = { onDeleteItem(item.idCartItem) }
                )
                if (index < group.items.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItemUiModel,
    onCheckedChange: (Boolean) -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Checkbox(checked = item.isChecked, onCheckedChange = onCheckedChange, modifier = Modifier.padding(top = 2.dp))

        Image(
            painter = painterResource(id = item.imageResId),
            contentDescription = item.productName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = item.productName, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 18.sp)
            Text(text = "Rp ${item.price.toLong()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                QuantityStepper(quantity = item.quantity, onIncrease = onIncrease, onDecrease = onDecrease)
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun QuantityStepper(quantity: Int, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedIconButton(onClick = onDecrease, modifier = Modifier.size(28.dp), shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp)) {
            Text("−", fontSize = 14.sp)
        }
        Box(modifier = Modifier.width(36.dp).height(28.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
            Text(quantity.toString(), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        OutlinedIconButton(onClick = onIncrease, modifier = Modifier.size(28.dp), shape = RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)) {
            Text("+", fontSize = 14.sp)
        }
    }
}

@Composable
private fun CartEmptyState(modifier: Modifier = Modifier, onShopClick: () -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("🛒", fontSize = 64.sp)
            Text("Keranjangmu masih kosong", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text("Yuk, mulai belanja sekarang!", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = onShopClick) { Text("Mulai Belanja") }
        }
    }
}

@Composable
private fun CartBottomBar(
    isAllChecked: Boolean,
    totalPrice: Double,
    checkedCount: Int,
    onAllCheckedChange: (Boolean) -> Unit,
    onCheckout: () -> Unit
) {
    Surface(tonalElevation = 8.dp, shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Checkbox(checked = isAllChecked, onCheckedChange = onAllCheckedChange)
                Text("Semua", fontSize = 13.sp)
            }
            Text("Rp ${totalPrice.toLong()}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = onCheckout, shape = RoundedCornerShape(8.dp), enabled = checkedCount > 0) {
                Text("Beli ($checkedCount)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}