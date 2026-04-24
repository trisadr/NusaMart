package com.example.nusamart.feature.buyer.cart

import android.content.Context
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.entity.Cart
import com.example.nusamart.entity.Order
import com.example.nusamart.entity.OrderItem
import com.example.nusamart.entity.OrderStatus
import com.example.nusamart.entity.Product
import com.example.nusamart.feature.buyer.homepage.loadProductsFromJson
import com.example.nusamart.ui.theme.NusaMartTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.UUID

// ==========================================
// DATA LOADING & SAVING
// ==========================================

fun loadCartItems(context: Context): List<Cart> {
    return try {
        // Baca dari filesDir dulu (sudah ada perubahan), fallback ke assets
        val cartFile = File(context.filesDir, "cart.json")
        val jsonString = if (cartFile.exists()) {
            cartFile.readText()
        } else {
            context.assets.open("cart.json").bufferedReader().use { it.readText() }
        }
        val type = object : TypeToken<List<Cart>>() {}.type
        Gson().fromJson(jsonString, type) ?: emptyList()
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

fun saveCartItems(context: Context, items: List<Cart>) {
    try {
        val cartFile = File(context.filesDir, "cart.json")
        cartFile.writeText(Gson().toJson(items))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun saveNewOrder(context: Context, order: Order, orderItems: List<OrderItem>) {
    try {
        // Simpan order
        val orderFile = File(context.filesDir, "order.json")
        val existingOrders: MutableList<Order> = if (orderFile.exists()) {
            val type = object : TypeToken<List<Order>>() {}.type
            Gson().fromJson(orderFile.readText(), type) ?: mutableListOf()
        } else {
            // Baca dari assets sebagai base
            try {
                val type = object : TypeToken<List<Order>>() {}.type
                val json = context.assets.open("order.json").bufferedReader().use { it.readText() }
                Gson().fromJson(json, type) ?: mutableListOf()
            } catch (e: Exception) {
                mutableListOf()
            }
        }
        existingOrders.add(order)
        orderFile.writeText(Gson().toJson(existingOrders))

        // Simpan order items
        val orderItemFile = File(context.filesDir, "order_item.json")
        val existingItems: MutableList<OrderItem> = if (orderItemFile.exists()) {
            val type = object : TypeToken<List<OrderItem>>() {}.type
            Gson().fromJson(orderItemFile.readText(), type) ?: mutableListOf()
        } else {
            try {
                val type = object : TypeToken<List<OrderItem>>() {}.type
                val json = context.assets.open("order_item.json").bufferedReader().use { it.readText() }
                Gson().fromJson(json, type) ?: mutableListOf()
            } catch (e: Exception) {
                mutableListOf()
            }
        }
        existingItems.addAll(orderItems)
        orderItemFile.writeText(Gson().toJson(existingItems))

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// ==========================================
// STATEFUL SCREEN
// ==========================================

@Composable
fun CartScreen() {
    val context = LocalContext.current
    val backStack = LocalBackStack.current

    // Load cart & product dari JSON, simpan sebagai state agar UI reaktif
    val initialCart = remember { loadCartItems(context) }
    val products = remember { loadProductsFromJson(context) }
    val productMap = remember { products.associateBy { it.idProduct } }

    var cartItems by remember { mutableStateOf(initialCart) }

    // Hitung total dari item yang dicentang
    val checkedItems = cartItems.filter { it.isChecked }
    val totalPrice = checkedItems.sumOf { cart ->
        val price = productMap[cart.idProduct]?.price ?: 0.0
        price * cart.quantity
    }

    Content(
        cartItems = cartItems,
        productMap = productMap,
        totalPrice = totalPrice,

        onBackClick = {
            if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex)
        },

        onCheckedChange = { idCart, checked ->
            cartItems = cartItems.map {
                if (it.idCart == idCart) it.copy(isChecked = checked) else it
            }
            saveCartItems(context, cartItems)
        },

        onAllCheckedChange = { checked ->
            cartItems = cartItems.map { it.copy(isChecked = checked) }
            saveCartItems(context, cartItems)
        },

        onQuantityIncrease = { idCart ->
            cartItems = cartItems.map {
                if (it.idCart == idCart) it.copy(quantity = it.quantity + 1) else it
            }
            saveCartItems(context, cartItems)
        },

        onQuantityDecrease = { idCart ->
            cartItems = cartItems.map {
                if (it.idCart == idCart && it.quantity > 1)
                    it.copy(quantity = it.quantity - 1)
                else it
            }
            saveCartItems(context, cartItems)
        },

        onDeleteItem = { idCart ->
            cartItems = cartItems.filter { it.idCart != idCart }
            saveCartItems(context, cartItems)
        },

        onCheckout = {
            if (checkedItems.isEmpty()) return@Content

            // Buat Order baru
            val newOrderId = "ORD-${UUID.randomUUID().toString().take(8).uppercase()}"
            val sellerId = productMap[checkedItems.first().idProduct]?.idSeller ?: ""

            val newOrder = Order(
                idOrder = newOrderId,
                totalPrice = totalPrice,
                status = OrderStatus.MENUNGGU,
                trackingNumber = "",
                description = "",
                orderDate = System.currentTimeMillis(),
                arrivedDate = null,
                idSeller = sellerId
            )

            // Buat OrderItem untuk tiap cart item yang dicentang
            val newOrderItems = checkedItems.mapIndexed { index, cart ->
                OrderItem(
                    idOrderItem = "OI-${newOrderId}-${index + 1}",
                    idOrder = newOrderId,
                    idProduct = cart.idProduct,
                    quantity = cart.quantity,
                    priceAtPurchase = productMap[cart.idProduct]?.price ?: 0.0
                )
            }

            // Simpan order & order items ke filesDir
            saveNewOrder(context, newOrder, newOrderItems)

            // Hapus item yang sudah dicheckout dari cart
            cartItems = cartItems.filter { !it.isChecked }
            saveCartItems(context, cartItems)

            // Navigasi ke PaymentScreen dengan orderId
            backStack.add(Routes.PaymentRoute(newOrderId))
        }
    )
}

// ==========================================
// STATELESS CONTENT
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    cartItems: List<Cart>,
    productMap: Map<String, Product>,
    totalPrice: Double,
    onBackClick: () -> Unit,
    onCheckedChange: (String, Boolean) -> Unit,
    onAllCheckedChange: (Boolean) -> Unit,
    onQuantityIncrease: (String) -> Unit,
    onQuantityDecrease: (String) -> Unit,
    onDeleteItem: (String) -> Unit,
    onCheckout: () -> Unit
) {
    val checkedCount = cartItems.count { it.isChecked }
    val isAllChecked = cartItems.isNotEmpty() && cartItems.all { it.isChecked }
    val grouped = cartItems.groupBy { productMap[it.idProduct]?.idStore ?: "Toko Lainnya" }

    Scaffold(
        topBar = {
            CartTopBar(
                itemCount = cartItems.size,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            CartBottomBar(
                isAllChecked = isAllChecked,
                totalPrice = totalPrice,
                checkedCount = checkedCount,
                onAllCheckedChange = onAllCheckedChange,
                onCheckout = onCheckout
            )
        }
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            CartEmptyState(modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                grouped.forEach { (storeName, storeItems) ->
                    item(key = storeName) {
                        ShopGroup(
                            shopName = storeName,
                            items = storeItems,
                            productMap = productMap,
                            onCheckedChange = onCheckedChange,
                            onQuantityIncrease = onQuantityIncrease,
                            onQuantityDecrease = onQuantityDecrease,
                            onDeleteItem = onDeleteItem
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

// ==========================================
// TOP BAR
// ==========================================

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
                fontSize = 18.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
        }
    )
}

// ==========================================
// SHOP GROUP
// ==========================================

@Composable
private fun ShopGroup(
    shopName: String,
    items: List<Cart>,
    productMap: Map<String, Product>,
    onCheckedChange: (String, Boolean) -> Unit,
    onQuantityIncrease: (String) -> Unit,
    onQuantityDecrease: (String) -> Unit,
    onDeleteItem: (String) -> Unit
) {
    val allChecked = items.all { it.isChecked }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header toko
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = allChecked,
                    onCheckedChange = { checked ->
                        items.forEach { onCheckedChange(it.idCart, checked) }
                    }
                )
                Text(
                    text = shopName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            HorizontalDivider(thickness = 0.5.dp)

            items.forEachIndexed { index, cart ->
                val product = productMap[cart.idProduct]
                CartItemRow(
                    cart = cart,
                    product = product,
                    onCheckedChange = { checked -> onCheckedChange(cart.idCart, checked) },
                    onIncrease = { onQuantityIncrease(cart.idCart) },
                    onDecrease = { onQuantityDecrease(cart.idCart) },
                    onDelete = { onDeleteItem(cart.idCart) }
                )
                if (index < items.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

// ==========================================
// CART ITEM ROW
// ==========================================

@Composable
private fun CartItemRow(
    cart: Cart,
    product: Product?,
    onCheckedChange: (Boolean) -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Checkbox(
            checked = cart.isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(top = 2.dp)
        )

        // Gambar produk (placeholder)
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = product?.name ?: cart.idProduct,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Text(
                text = "Rp ${product?.price?.toLong() ?: 0}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuantityStepper(
                    quantity = cart.quantity,
                    onIncrease = onIncrease,
                    onDecrease = onDecrease
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// QUANTITY STEPPER
// ==========================================

@Composable
private fun QuantityStepper(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedIconButton(
            onClick = onDecrease,
            modifier = Modifier.size(28.dp),
            shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp)
        ) {
            Text(text = "−", fontSize = 14.sp)
        }

        Box(
            modifier = Modifier
                .width(36.dp)
                .height(28.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = quantity.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        OutlinedIconButton(
            onClick = onIncrease,
            modifier = Modifier.size(28.dp),
            shape = RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
        ) {
            Text(text = "+", fontSize = 14.sp)
        }
    }
}

// ==========================================
// EMPTY STATE
// ==========================================

@Composable
private fun CartEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "🛒", fontSize = 64.sp)
            Text(
                text = "Keranjangmu masih kosong",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = "Yuk, mulai belanja sekarang!",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = {}) {
                Text(text = "Mulai Belanja")
            }
        }
    }
}

// ==========================================
// BOTTOM BAR
// ==========================================

@Composable
private fun CartBottomBar(
    isAllChecked: Boolean,
    totalPrice: Double,
    checkedCount: Int,
    onAllCheckedChange: (Boolean) -> Unit,
    onCheckout: () -> Unit
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = isAllChecked,
                    onCheckedChange = onAllCheckedChange
                )
                Text(text = "Semua", fontSize = 13.sp)
            }

            Text(
                text = "Rp ${totalPrice.toLong()}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = onCheckout,
                shape = RoundedCornerShape(8.dp),
                enabled = checkedCount > 0
            ) {
                Text(
                    text = "Beli ($checkedCount)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// ==========================================
// PREVIEW
// ==========================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CartPreview() {
    val dummyCart = listOf(
        Cart("CART-001", "BUYER-001", "PROD-001", 2, true),
        Cart("CART-002", "BUYER-001", "PROD-002", 1, true),
        Cart("CART-003", "BUYER-001", "PROD-003", 3, false)
    )
    val dummyProducts = mapOf(
        "PROD-001" to Product("PROD-001", "Beras Raja Lele 5kg", 75000.0, "", 50, "", 0, "SELL-001", "STORE-001", "Solo"),
        "PROD-002" to Product("PROD-002", "Minyak Goreng Sunco 2L", 38000.0, "", 30, "", 0, "SELL-002", "STORE-002", "Solo"),
        "PROD-003" to Product("PROD-003", "Minyak Goreng Palsu", 1000.0, "", 30, "", 0, "SELL-002", "STORE-002", "Solo")
    )

    NusaMartTheme(dynamicColor = false) {
        Content(
            cartItems = dummyCart,
            productMap = dummyProducts,
            totalPrice = 188000.0,
            onBackClick = {},
            onCheckedChange = { _, _ -> },
            onAllCheckedChange = {},
            onQuantityIncrease = {},
            onQuantityDecrease = {},
            onDeleteItem = {},
            onCheckout = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CartEmptyPreview() {
    NusaMartTheme(dynamicColor = false) {
        Content(
            cartItems = emptyList(),
            productMap = emptyMap(),
            totalPrice = 0.0,
            onBackClick = {},
            onCheckedChange = { _, _ -> },
            onAllCheckedChange = {},
            onQuantityIncrease = {},
            onQuantityDecrease = {},
            onDeleteItem = {},
            onCheckout = {}
        )
    }
}