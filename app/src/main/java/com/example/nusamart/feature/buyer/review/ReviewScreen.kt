package com.example.nusamart.feature.buyer.review

import android.content.Context
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nusamart.R
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.entity.Order
import com.example.nusamart.entity.OrderItem
import com.example.nusamart.entity.OrderStatus
import com.example.nusamart.entity.Product
import com.example.nusamart.entity.Review
import com.example.nusamart.feature.buyer.homepage.loadProductsFromJson
import com.example.nusamart.ui.theme.NusaMartTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.UUID

// ==========================================
// DATA LOADING
// ==========================================

fun loadOrderById(context: Context, orderId: String): Order? {
    return try {
        val jsonString = context.assets.open("order.json")
            .bufferedReader()
            .use { it.readText() }
        val type = object : TypeToken<List<Order>>() {}.type
        val orders: List<Order> = Gson().fromJson(jsonString, type) ?: emptyList()
        orders.find { it.idOrder == orderId }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun loadOrderItemsByOrderId(context: Context, orderId: String): List<OrderItem> {
    return try {
        val jsonString = context.assets.open("order_item.json")
            .bufferedReader()
            .use { it.readText() }
        val type = object : TypeToken<List<OrderItem>>() {}.type
        val items: List<OrderItem> = Gson().fromJson(jsonString, type) ?: emptyList()
        items.filter { it.idOrder == orderId }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

fun saveReviewsToJson(context: Context, newReviews: List<Review>) {
    try {
        // Baca review yang sudah ada dari files dir (writable)
        val reviewFile = File(context.filesDir, "review.json")
        val existingReviews: MutableList<Review> = if (reviewFile.exists()) {
            val type = object : TypeToken<List<Review>>() {}.type
            Gson().fromJson(reviewFile.readText(), type) ?: mutableListOf()
        } else {
            mutableListOf()
        }

        existingReviews.addAll(newReviews)
        reviewFile.writeText(Gson().toJson(existingReviews))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// State per item untuk menyimpan rating dan review text masing-masing produk
data class ReviewItemState(
    val rating: Int = 0,
    val reviewText: String = ""
)

// ==========================================
// STATEFUL SCREEN
// ==========================================

@Composable
fun ReviewScreen(orderId: String) {
    val context = LocalContext.current
    val backStack = LocalBackStack.current

    val order = remember { loadOrderById(context, orderId) }
    val orderItems = remember { loadOrderItemsByOrderId(context, orderId) }
    val products = remember { loadProductsFromJson(context) }

    // Map idProduct -> Product untuk lookup cepat
    val productMap = remember { products.associateBy { it.idProduct } }

    if (order == null) {
        Text("Order tidak ditemukan")
        return
    }

    // State review untuk setiap order item, key = idOrderItem
    val reviewStates = remember {
        orderItems.associate { item ->
            item.idOrderItem to mutableStateOf(ReviewItemState())
        }
    }

    Content(
        order = order,
        orderItems = orderItems,
        productMap = productMap,
        reviewStates = reviewStates.mapValues { it.value.value },

        onRatingChange = { idOrderItem, rating ->
            reviewStates[idOrderItem]?.value =
                reviewStates[idOrderItem]!!.value.copy(rating = rating)
        },

        onReviewTextChange = { idOrderItem, text ->
            reviewStates[idOrderItem]?.value =
                reviewStates[idOrderItem]!!.value.copy(reviewText = text)
        },

        onBackClick = {
            if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex)
        },

        onSubmitClick = {
            // Validasi: semua item wajib ada rating
            val allRated = orderItems.all { item ->
                (reviewStates[item.idOrderItem]?.value?.rating ?: 0) > 0
            }

            if (!allRated) return@Content false

            // Buat list Review dari semua order item
            val reviews = orderItems.map { item ->
                val state = reviewStates[item.idOrderItem]!!.value
                Review(
                    idReview = UUID.randomUUID().toString(),
                    idOrder = orderId,
                    idProduct = item.idProduct,
                    imageResId = null,
                    rating = state.rating,
                    reviewProduct = state.reviewText.ifBlank { null }
                )
            }

            saveReviewsToJson(context, reviews)

            // Kembali ke OrderDetail
            backStack.removeAt(backStack.lastIndex)
            backStack.add(Routes.OrderDetailRoute(orderId))

            true
        }
    )
}

// ==========================================
// STATELESS CONTENT
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    order: Order,
    orderItems: List<OrderItem>,
    productMap: Map<String, Product>,
    reviewStates: Map<String, ReviewItemState>,
    onRatingChange: (String, Int) -> Unit,
    onReviewTextChange: (String, String) -> Unit,
    onBackClick: () -> Unit,
    onSubmitClick: () -> Boolean
) {
    var showRatingError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tulis Ulasan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    if (showRatingError) {
                        Text(
                            text = "Harap beri bintang untuk semua produk",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Button(
                        onClick = {
                            val success = onSubmitClick()
                            if (!success) showRatingError = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Kirim Ulasan")
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
            orderItems.forEachIndexed { index, orderItem ->
                val product = productMap[orderItem.idProduct]
                val state = reviewStates[orderItem.idOrderItem] ?: ReviewItemState()

                ReviewItemSection(
                    product = product,
                    orderItem = orderItem,
                    state = state,
                    onRatingChange = { rating -> onRatingChange(orderItem.idOrderItem, rating) },
                    onReviewTextChange = { text -> onReviewTextChange(orderItem.idOrderItem, text) }
                )

                if (index < orderItems.lastIndex) {
                    HorizontalDivider(thickness = 8.dp, color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
        }
    }
}

// ==========================================
// REVIEW ITEM SECTION (per produk)
// ==========================================

@Composable
private fun ReviewItemSection(
    product: Product?,
    orderItem: OrderItem,
    state: ReviewItemState,
    onRatingChange: (Int) -> Unit,
    onReviewTextChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Info produk singkat
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    id = if (product?.imageResId != null && product.imageResId != 0)
                        product.imageResId
                    else
                        R.drawable.keranjang
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product?.name ?: orderItem.idProduct,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${orderItem.quantity} barang",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Rating bintang
        Text(
            text = "Bagaimana kualitas produk ini?",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.Start) {
            repeat(5) { index ->
                val starValue = index + 1
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Bintang $starValue",
                    tint = if (starValue <= state.rating) Color(0xFFFFC107) else Color.LightGray,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .clickable { onRatingChange(starValue) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Upload foto (placeholder, belum ada aksi)
        Text(
            text = "Tambahkan Foto (Opsional)",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { /* TODO: buka galeri */ },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tambah",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Teks ulasan (opsional)
        Text(
            text = "Tulis Ulasan (Opsional)",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.reviewText,
            onValueChange = onReviewTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = {
                Text("Ceritakan pengalamanmu menggunakan produk ini...")
            },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

// ==========================================
// PREVIEW
// ==========================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReviewScreenPreview() {
    val dummyOrder = Order(
        idOrder = "ORD12345",
        totalPrice = 235000.0,
        status = OrderStatus.SELESAI,
        trackingNumber = "JNE123456789",
        description = "Pesanan produk kebutuhan sehari-hari",
        orderDate = 1713600000L,
        arrivedDate = null,
        idSeller = "SELLER001"
    )

    val dummyItems = listOf(
        OrderItem("OI001", "ORD12345", "PRD001", 2, 50000.0),
        OrderItem("OI002", "ORD12345", "PRD002", 1, 75000.0)
    )

    val dummyProducts = mapOf(
        "PRD001" to Product("PRD001", "Beras Makmur 5kg", 50000.0, "Beras premium", 10, "", R.drawable.keranjang, "S1", "T1", "Solo"),
        "PRD002" to Product("PRD002", "Keranjang Anyaman", 75000.0, "Keranjang kuat", 5, "", R.drawable.keranjang, "S2", "T2", "Solo")
    )

    val dummyStates = mapOf(
        "OI001" to ReviewItemState(rating = 4, reviewText = "Produk bagus!"),
        "OI002" to ReviewItemState(rating = 0, reviewText = "")
    )

    NusaMartTheme(dynamicColor = false) {
        Content(
            order = dummyOrder,
            orderItems = dummyItems,
            productMap = dummyProducts,
            reviewStates = dummyStates,
            onRatingChange = { _, _ -> },
            onReviewTextChange = { _, _ -> },
            onBackClick = {},
            onSubmitClick = { true }
        )
    }
}