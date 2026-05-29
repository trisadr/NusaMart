package com.example.nusamart.feature.buyer.homepage.search

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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.feature.buyer.homepage.ProductGridCard
import com.example.nusamart.feature.components.BottomMenu
import com.example.nusamart.feature.components.NusaMartBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    initialKeyword: String,
    vm: SearchResultVM = hiltViewModel()
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    // Kirim keyword awal ke ViewModel saat layar pertama kali dibuka
    LaunchedEffect(Unit) {
        vm.initialize(initialKeyword)
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
                Column(modifier = Modifier.statusBarsPadding().padding(bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }

                        OutlinedTextField(
                            value = uiState.currentQuery,
                            onValueChange = vm::updateSearchQuery,
                            modifier = Modifier.weight(1f).heightIn(min = 40.dp),
                            placeholder = { Text("Cari produk...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    FilterRow(
                        selectedFilter = uiState.selectedFilter,
                        onFilterSelected = vm::updateFilter
                    )
                }
            }
        },
        bottomBar = {
            NusaMartBottomNavigation(
                selectedMenu = null,
                onMenuSelected = { menu ->
                    when (menu) {
                        BottomMenu.HOME -> backStack.add(Routes.HomeRoute)
                        BottomMenu.NOTIFICATION -> backStack.add(Routes.NotificationRoute)
                        BottomMenu.PROFILE -> backStack.add(Routes.ProfileRoute)
                        BottomMenu.CART -> backStack.add(Routes.CartRoute)
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.filteredProducts.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("Produk \"${uiState.currentQuery}\" tidak ditemukan")
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                ) {
                    items(uiState.filteredProducts) { product ->
                        ProductGridCard(
                            product = product,
                            onClick = { backStack.add(Routes.ProductPageRoute(product.idProduct)) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterRow(selectedFilter: String, onFilterSelected: (String) -> Unit) {
    val filters = listOf("Semua", "Harga Termurah", "Harga Termahal")

    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                leadingIcon = if (selectedFilter == filter) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}