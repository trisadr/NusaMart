//package com.example.nusamart.feature.screen
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.heightIn
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.statusBarsPadding
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.FilterChip
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.example.nusamart.R
//import com.example.nusamart.core.LocalBackStack
//import com.example.nusamart.core.Routes
//import com.example.nusamart.entity.Product
//import com.example.nusamart.ui.theme.NusaMartTheme
//
//// ==========================================
//// 1. STATEFUL SCREEN (Yang dipanggil di ComposeApp)
//// ==========================================
//@Composable
//fun SearchResultScreen(
//    initialKeyword: String
//) {
//    val context = LocalContext.current
//    val backStack = LocalBackStack.current
//    var productList by remember { mutableStateOf<List<Product>>(emptyList()) }
//
//    // Memuat data dari product.json saat layar dibuka
//    LaunchedEffect(Unit) {
//        productList = loadProductsFromJson(context) // Pastikan fungsi ini sudah ada (di Utils atau di file ini)
//    }
//
//    // Memanggil bagian Stateless untuk digambar
//    SearchResultContent(
//        initialKeyword = initialKeyword,
//        productList = productList,
//        onBackClick = { backStack.removeAt(backStack.lastIndex) },
//        onProductClick = { productId ->
//            backStack.add(Routes.ProductPageRoute(productId))
//        }
//    )
//}
//
//// ==========================================
//// 2. STATELESS CONTENT (UI Murni, bebas dari context/navigasi rumit)
//// ==========================================
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SearchResultContent(
//    initialKeyword: String,
//    productList: List<Product>,
//    onBackClick: () -> Unit,
//    onProductClick: (String) -> Unit
//) {
//    var selectedFilter by remember { mutableStateOf("Semua") }
//    var currentQuery by remember { mutableStateOf(initialKeyword) }
//
//    // Filter data berdasarkan keyword dan filter pilihan
//    val searchedProduct = remember(currentQuery, selectedFilter, productList) {
//        val baseList = productList.filter {
//            it.name.contains(currentQuery, ignoreCase = true)
//        }
//
//        when (selectedFilter) {
//            "Harga termurah" -> baseList.sortedBy { it.price }
//            "Harga termahal" -> baseList.sortedByDescending { it.price }
//            else -> baseList
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            Surface(
//                shadowElevation = 4.dp,
//                color = MaterialTheme.colorScheme.surface
//            ) {
//                Column(
//                    modifier = Modifier
//                        .statusBarsPadding()
//                        .padding(bottom = 8.dp)
//                ) {
//                    // Search Bar
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(start = 4.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        IconButton(onClick = onBackClick) {
//                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//                        }
//
//                        OutlinedTextField(
//                            value = currentQuery,
//                            onValueChange = { currentQuery = it }, // Real-time update pencarian
//                            modifier = Modifier
//                                .weight(1f)
//                                .heightIn(min = 40.dp),
//                            placeholder = { Text("Cari produk...") },
//                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
//                            shape = RoundedCornerShape(12.dp),
//                            singleLine = true,
//                        )
//                    }
//
//                    FilterProduct(
//                        selectedFilter = selectedFilter,
//                        onFilterSelected = { selectedFilter = it }
//                    )
//                }
//            }
//        }
//    ) { innerPadding ->
//        if (productList.isEmpty()) {
//            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
//                CircularProgressIndicator() // Loading jika data JSON sedang diambil
//            }
//        } else if (searchedProduct.isEmpty()) {
//            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
//                Text("Produk '$currentQuery' tidak ditemukan")
//            }
//        } else {
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                contentPadding = PaddingValues(16.dp),
//                horizontalArrangement = Arrangement.spacedBy(12.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp),
//                modifier = Modifier.padding(innerPadding)
//            ) {
//                items(searchedProduct) { item ->
//                    ProductGridCard(
//                        product = item,
//                        onClick = { onProductClick(item.idProduct) }
//                    )
//                }
//            }
//        }
//    }
//}
//
//// ... (Fungsi FilterProduct biarkan sama seperti kodemu) ...
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FilterProduct(
//    selectedFilter: String,
//    onFilterSelected: (String) -> Unit
//) {
//    val filters = listOf("Semua", "Terlaris", "Harga termurah", "Harga termahal")
//
//    LazyRow(
//        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
//        horizontalArrangement = Arrangement.spacedBy(8.dp),
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(MaterialTheme.colorScheme.surface)
//    ) {
//        items(filters) { filter ->
//            FilterChip(
//                selected = (selectedFilter == filter),
//                onClick = { onFilterSelected(filter) },
//                label = { Text(filter) },
//                leadingIcon = if (selectedFilter == filter) {
//                    {
//                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
//                    }
//                } else null,
//                shape = RoundedCornerShape(20.dp)
//            )
//        }
//    }
//}
//
//// ==========================================
//// 3. PREVIEW (Memasukkan Mock Data agar aman)
//// ==========================================
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun SearchResultPreview() {
//    NusaMartTheme(dynamicColor = false) {
//
//        // Buat data tiruan yang strukturnya sama dengan JSON
//        val dummyJsonData = listOf(
//            Product(
//                idProduct = "PROD-1", name = "Beras Makmur", price = 60000.0,
//                description = "Beras kualitas baik", stock = 10, map = "",
//                imageResId = R.drawable.nm_logo, idSeller = "S1", idStore = "T1", location = "Surakarta"
//            ),
//            Product(
//                idProduct = "PROD-2", name = "Keranjang Anyaman", price = 25000.0,
//                description = "Keranjang kuat", stock = 5, map = "",
//                imageResId = R.drawable.nm_logo, idSeller = "S2", idStore = "T2", location = "Sukoharjo"
//            )
//        )
//
//        // Panggil bagian Stateless-nya, bukan Stateful-nya
//        SearchResultContent(
//            initialKeyword = "Keranjang",
//            productList = dummyJsonData,
//            onBackClick = {},
//            onProductClick = {}
//        )
//    }
//}