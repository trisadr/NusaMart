package com.example.nusamart.feature.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.nusamart.feature.entity.Product
import com.example.nusamart.feature.screen.ProductPageScreen
import com.example.nusamart.ui.theme.NusaMartTheme

class ProductPageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val passedProduct = Product(
            id = intent.getIntExtra("EXTRA_ID", 0),
            name = intent.getStringExtra("EXTRA_NAME") ?: "Produk tidak ditemukan",
            price = intent.getFloatExtra("EXTRA_PRICE", 0f),
            location = intent.getStringExtra("EXTRA_LOCATION") ?: "-",
            imageRes = intent.getIntExtra("EXTRA_IMAGE", android.R.drawable.ic_menu_gallery),
            description = intent.getStringExtra("EXTRA_DESC") ?: "Tidak ada deskripsi.",
            map = intent.getStringExtra("EXTRA_MAP") ?: "Tidak ada lokasi terdaftar."
        )

        setContent {
            NusaMartTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProductPageScreen(
                        product = passedProduct,
                        onBackClick = { finish() }
                    )
                }
            }
        }
    }
}