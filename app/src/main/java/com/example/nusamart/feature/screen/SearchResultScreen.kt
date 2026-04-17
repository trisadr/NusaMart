package com.example.nusamart.feature.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nusamart.feature.entity.dummyProductList
import com.example.nusamart.ui.theme.NusaMartTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    keyword: String,
    onBackClick: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("Semua") }

    val searchdProduct = remember(keyword, selectedFilter) {
        val baseList = dummyProductList.filter {
            it.name.contains(keyword, ignoreCase = true)
        }

        when (selectedFilter) {
            "Harga termurah" -> baseList.sortedBy { it.price.toInt() }
            "Harga termahal" -> baseList.sortedByDescending { it.price.toInt() }
            else -> baseList
        }
    }

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding() // Agar tidak tertutup Notch/Status Bar HP
                        .padding(bottom = 8.dp) // Jarak bawah ke Filter
                ) {
                    // search bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }

                        OutlinedTextField(
                            value = keyword, //menampilkan search result by keyword
                            onValueChange = { /* jika ingin mencari produk lain (merubah keyword) */ },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 40.dp),
                            placeholder = { Text("Cari produk...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                        )
                    }

                    FilterProduct(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it }
                    )
                }
            }
        }
    ) { innerPadding ->
        if (searchdProduct.isEmpty()) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Produk '$keyword' tidak ditemukan")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(innerPadding)
            ) {
                items(searchdProduct) { item ->
                    ProductGridCard(product = item)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterProduct(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("Semua", "Terlaris", "Harga termurah", "Harga termahal")

    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = (selectedFilter == filter),
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                leadingIcon = if (selectedFilter == filter) {
                    {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                } else null,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

// PREVIEW
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SearchResultPreview() {
    NusaMartTheme(dynamicColor = false) {
        SearchResultScreen("keranjang", {})
    }
}