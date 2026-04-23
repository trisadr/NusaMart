package com.example.nusamart.feature.buyer.homepage

//import com.example.nusamart.entity.Product // Pastikan import data class Product kamu benar
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nusamart.R
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.entity.Product
import com.example.nusamart.feature.components.BottomMenu
import com.example.nusamart.feature.components.NusaMartBottomNavigation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Fungsi helper untuk membaca JSON dari Assets
fun loadProductsFromJson(context: Context): List<Product> {
    return try {
        val jsonString = context.assets.open("product.json").bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<Product>>() {}.type
        Gson().fromJson(jsonString, listType)
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageScreen(
    isLoading: Boolean = false
) {
    val backStack = LocalBackStack.current
    val context = LocalContext.current

    var productList by remember { mutableStateOf<List<Product>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        productList = loadProductsFromJson(context)
    }

    Content(
        isLoading = isLoading,
        productList = productList,
        searchQuery = searchQuery,
        onSearchChange = { searchQuery = it },
        onSearch = {
            if (searchQuery.isNotBlank()) {
                backStack.add(Routes.SearchResultRoute(searchQuery))
            }
        },
        onProductClick = { productId ->
            backStack.add(Routes.ProductPageRoute(productId))
        },
        onMenuSelected = { menu ->
            when (menu) {
                BottomMenu.HOME -> Unit
                BottomMenu.NOTIFICATION -> {
                    backStack.add(Routes.NotificationRoute)
                }
                BottomMenu.PROFILE -> {
                    backStack.add(Routes.ProfileRoute)
                }
                BottomMenu.CART -> {
                    backStack.add(Routes.CartRoute)
                }

            }
        }

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    isLoading: Boolean,
    productList: List<Product>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSearch: () -> Unit,
    onProductClick: (String) -> Unit,
    onMenuSelected: (BottomMenu) -> Unit
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nusamart),
                    contentDescription = "Logo NusaMart",
                    modifier = Modifier.height(32.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Cari produk lokal...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search Icon")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { onSearch() }
                    )
                )
            }
        },
        bottomBar = {
            NusaMartBottomNavigation(
                selectedMenu = BottomMenu.HOME,
                onMenuSelected = onMenuSelected
            )
        }
    ) { innerPadding ->

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(innerPadding)
            ) {
                items(productList) { produk ->
                    ProductGridCard(
                        product = produk,
                        onClick = { onProductClick(produk.idProduct) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductGridCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.nm_logo),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Rp ${product.price.toLong()}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.location,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePagePreview() {
    Content(
        isLoading = false,
        productList = listOf(
            Product(
                idProduct = "1",
                name = "Kopi Gayo Premium",
                price = 75000.0,
                description = "Kopi asli Aceh dengan rasa khas",
                stock = 10,
                map = "Aceh",
                imageResId = R.drawable.nm_logo,
                idSeller = "S1",
                idStore = "Store1",
                location = "Aceh"
            ),
            Product(
                idProduct = "2",
                name = "Batik Tulis",
                price = 150000.0,
                description = "Batik handmade khas Jogja",
                stock = 5,
                map = "Yogyakarta",
                imageResId = R.drawable.nm_logo,
                idSeller = "S2",
                idStore = "Store2",
                location = "Yogyakarta"
            )
        ),
        searchQuery = "",
        onSearchChange = {},
        onSearch = {},
        onProductClick = {},
        onMenuSelected = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun HomePageLoadingPreview() {
    Content(
        isLoading = true,
        productList = emptyList(),
        searchQuery = "",
        onSearchChange = {},
        onSearch = {},
        onProductClick = {},
        onMenuSelected = {}
    )
}