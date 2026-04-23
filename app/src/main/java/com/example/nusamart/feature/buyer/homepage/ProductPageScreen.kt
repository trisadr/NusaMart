package com.example.nusamart.feature.buyer.homepage

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nusamart.R
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.entity.Product
import com.example.nusamart.ui.theme.NusaMartTheme
import java.text.NumberFormat
import java.util.Locale

// ── Stateful ────────────────────────────────────────────────────────────────

@Composable
fun ProductPageScreen(productId: String) {
    val context = LocalContext.current
    val backStack = LocalBackStack.current

    var product by remember { mutableStateOf<Product?>(null) }
    var quantity by remember { mutableStateOf(1) }

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

    ProductPageContent(
        product = product!!,
        quantity = quantity,
        onIncrease = { if (quantity < product!!.stock) quantity++ },
        onDecrease = { if (quantity > 1) quantity-- },
        onBackClick = {
            backStack.removeLastOrNull()
        },
        onShareClick = {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "${product!!.name} - ${formatPrice(product!!.price)}")
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
            // TODO: navigasi ke chat
            Toast.makeText(context, "Fitur chat segera hadir", Toast.LENGTH_SHORT).show()
        },
        onAddToCart = {
            // TODO: tambahkan ke cart via ViewModel/repository
            Toast.makeText(context, "${product!!.name} ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
        },
        onBuyNow = {
//            backStack.add(Routes.PaymentRoute(product!!.idProduct, quantity)) // payment sementara belum bisa
        }
    )
}

// ── Stateless ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPageContent(
    product: Product,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onOpenMap: () -> Unit,
    onChatClick: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Produk") },
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
            BottomAppBar(modifier = Modifier.height(64.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onChatClick) {
                        Icon(Icons.Default.MailOutline, contentDescription = "Chat Penjual")
                    }
                    IconButton(onClick = onAddToCart) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Tambah ke Keranjang")
                    }
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = onBuyNow,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Beli Sekarang")
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
            // Foto Produk
            Image(
                painter = painterResource(product.imageResId),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

            Column(modifier = Modifier.padding(16.dp)) {

                // Harga
                Text(
                    text = formatPrice(product.price),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(4.dp))

                // Nama produk
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                // Link lokasi
                Text(
                    text = "Dikirim dari: ${product.location}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onOpenMap() }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Info toko
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
                    Column {
                        Text(
                            text = product.idStore,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Pilih jumlah
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Jumlah",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedIconButton(onClick = onDecrease, enabled = quantity > 1) {
                        Text("-", fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = quantity.toString(),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    OutlinedIconButton(onClick = onIncrease, enabled = quantity < product.stock) {
                        Text("+", fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "Stok: ${product.stock}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Deskripsi
                Text(
                    text = "Deskripsi Produk",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ── Helper ───────────────────────────────────────────────────────────────────

fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp ${formatter.format(price.toLong())}"
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductPagePreview() {
    NusaMartTheme {
        ProductPageContent(
            product = Product(
                idProduct = "1",
                name = "Sepatu Contoh Preview",
                price = 150000.0,
                description = "Ini adalah teks deskripsi produk yang cukup panjang untuk melihat tampilannya.",
                stock = 10,
                map = "https://maps.google.com",
                imageResId = R.drawable.nm_logo,
                idSeller = "S1",
                idStore = "Toko Contoh",
                location = "Kota Jakarta"
            ),
            quantity = 1,
            onIncrease = {},
            onDecrease = {},
            onBackClick = {},
            onShareClick = {},
            onOpenMap = {},
            onChatClick = {},
            onAddToCart = {},
            onBuyNow = {}
        )
    }
}