package com.example.nusamart.feature.buyer.homepage

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nusamart.R
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.entity.Cart
import com.example.nusamart.entity.Order
import com.example.nusamart.entity.OrderItem
import com.example.nusamart.entity.OrderStatus
import com.example.nusamart.entity.Product
import com.example.nusamart.feature.buyer.cart.loadCartItems
import com.example.nusamart.feature.buyer.cart.saveCartItems
import com.example.nusamart.feature.buyer.cart.saveNewOrder
import com.example.nusamart.ui.theme.NusaMartTheme
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

enum class SheetMode {
    NONE, CART, BUY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPageScreen(productId: String) {
    val context = LocalContext.current
    val backStack = LocalBackStack.current

    var product by remember { mutableStateOf<Product?>(null) }
    var sheetMode by remember { mutableStateOf(SheetMode.NONE) }
    var quantity by remember { mutableIntStateOf(1) }

    LaunchedEffect(productId) {
        val list = loadProductsFromJson(context)
        product = list.find { it.idProduct == productId }
    }

    if (product == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Content(
        product = product!!,
        sheetMode = sheetMode,
        quantity = quantity,

        onCartIconClick = {
            sheetMode = SheetMode.CART
            quantity = 1
        },

        onBuyNowMainClick = {
            sheetMode = SheetMode.BUY
            quantity = 1
        },

        onDismissSheet = { sheetMode = SheetMode.NONE },
        onIncrease = { if (quantity < product!!.stock) quantity++ },
        onDecrease = { if (quantity > 1) quantity-- },

        onConfirmCart = {
            val currentCart = loadCartItems(context).toMutableList()
            val existing = currentCart.find { it.idProduct == productId }

            if (existing != null) {
                val updated = currentCart.map {
                    if (it.idProduct == productId)
                        it.copy(quantity = it.quantity + quantity)
                    else it
                }
                saveCartItems(context, updated)
            } else {
                val newCartItem = Cart(
                    idCart = "CART-${UUID.randomUUID().toString().take(8).uppercase()}",
                    idBuyer = "BUYER-001", // TODO: ganti dengan activeUser?.idBuyer saat sudah ada
                    idProduct = productId,
                    quantity = quantity,
                    isChecked = true
                )
                currentCart.add(newCartItem)
                saveCartItems(context, currentCart)
            }

            sheetMode = SheetMode.NONE
            Toast.makeText(
                context,
                "$quantity ${product!!.name} masuk ke keranjang",
                Toast.LENGTH_SHORT
            ).show()
        },

        onConfirmBuy = {
            val newOrderId = "ORD-${UUID.randomUUID().toString().take(8).uppercase()}"

            val newOrder = Order(
                idOrder = newOrderId,
                totalPrice = product!!.price * quantity,
                status = OrderStatus.MENUNGGU,
                trackingNumber = "",
                description = "",
                orderDate = System.currentTimeMillis(),
                arrivedDate = null,
                idSeller = product!!.idSeller
            )

            val newOrderItem = OrderItem(
                idOrderItem = "OI-$newOrderId-1",
                idOrder = newOrderId,
                idProduct = productId,
                quantity = quantity,
                priceAtPurchase = product!!.price
            )

            saveNewOrder(context, newOrder, listOf(newOrderItem))

            sheetMode = SheetMode.NONE
            backStack.add(Routes.PaymentRoute(orderId = newOrderId))
        },

        onBackClick = {
            if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex)
        },

        onShareClick = {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Lihat produk ini di NusaMart: ${product!!.name} seharga ${formatPrice(product!!.price)}!"
                )
            }
            context.startActivity(Intent.createChooser(intent, "Bagikan produk"))
        },

        onOpenMap = {
            try {
                val uri = android.net.Uri.parse(product!!.map)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        },

        onChatClick = {
            Toast.makeText(context, "Fitur chat segera hadir", Toast.LENGTH_SHORT).show()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    product: Product,
    sheetMode: SheetMode,
    quantity: Int,
    onCartIconClick: () -> Unit,
    onBuyNowMainClick: () -> Unit,
    onDismissSheet: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onConfirmCart: () -> Unit,
    onConfirmBuy: () -> Unit,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onOpenMap: () -> Unit,
    onChatClick: () -> Unit
) {
    val redColor = Color(0xFFF05555)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Produk", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = onShareClick) {
                        Icon(Icons.Default.Share, contentDescription = "Bagikan")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(72.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onChatClick) {
                        Icon(
                            Icons.Default.MailOutline,
                            contentDescription = "Chat Penjual",
                            tint = Color.DarkGray
                        )
                    }
                    IconButton(onClick = onCartIconClick) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Tambah ke Keranjang",
                            tint = Color.DarkGray
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = onBuyNowMainClick,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = redColor)
                    ) {
                        Text("Beli Sekarang", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            val context = LocalContext.current
            val safeResId = remember(product.imageResId) {
                val resId = product.imageResId
                try {
                    if (resId != 0) {
                        context.resources.getResourceName(resId)
                        resId
                    } else null
                } catch (e: Exception) { null }
            }

            Image(
                painter = painterResource(safeResId ?: R.drawable.nm_logo),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.LightGray)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = formatPrice(product.price),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = redColor
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Stok: ${product.stock}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Dikirim dari: ${product.location}",
                    color = redColor,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onOpenMap() }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray)

                Text(
                    text = "Deskripsi Produk",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.drawable.nm_logo),
                        contentDescription = "Foto Toko",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = product.idStore,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (sheetMode != SheetMode.NONE) {
        ModalBottomSheet(
            onDismissRequest = onDismissSheet,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val context = LocalContext.current
                    val safeResId = remember(product.imageResId) {
                        val resId = product.imageResId
                        try {
                            if (resId != 0) {
                                context.resources.getResourceName(resId)
                                resId
                            } else null
                        } catch (e: Exception) { null }
                    }
                    Image(
                        painter = painterResource(safeResId ?: R.drawable.nm_logo),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = formatPrice(product.price),
                            color = Color(0xFFF05555),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Stok: ${product.stock}",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Jumlah",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedIconButton(
                        onClick = onDecrease,
                        enabled = quantity > 1,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text("−", fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = quantity.toString(),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    OutlinedIconButton(
                        onClick = onIncrease,
                        enabled = quantity < product.stock,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text("+", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    Text(text = "Total", color = Color.Gray)
                    Text(
                        text = formatPrice(product.price * quantity),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF05555)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = if (sheetMode == SheetMode.CART) onConfirmCart else onConfirmBuy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF05555))
                ) {
                    Text(
                        text = if (sheetMode == SheetMode.CART) "Masukkan ke Keranjang" else "Beli Sekarang",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp ${formatter.format(price.toLong())}"
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProductPagePreview() {
    NusaMartTheme {
        Content(
            product = Product(
                idProduct = "PROD-001",
                name = "Beras Raja Lele 5kg",
                price = 75000.0,
                description = "Beras pulen berkualitas tinggi dari petani lokal.",
                stock = 10,
                map = "https://maps.google.com",
                imageResId = R.drawable.nm_logo,
                idSeller = "SELL-001",
                idStore = "Toko Makmur",
                location = "Jebres, Surakarta"
            ),
            sheetMode = SheetMode.NONE,
            quantity = 1,
            onCartIconClick = {},
            onBuyNowMainClick = {},
            onDismissSheet = {},
            onIncrease = {},
            onDecrease = {},
            onConfirmCart = {},
            onConfirmBuy = {},
            onBackClick = {},
            onShareClick = {},
            onOpenMap = {},
            onChatClick = {}
        )
    }
}
