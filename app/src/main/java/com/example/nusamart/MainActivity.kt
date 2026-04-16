package com.example.nusamart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart // Icon belanja
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nusamart.feature.screen.HomePageScreen
import com.example.nusamart.ui.order.OrderListScreen
import com.example.nusamart.ui.order.OrderDetailScreen
import com.example.nusamart.ui.theme.NusaMartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NusaMartTheme(dynamicColor = false) {
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    floatingActionButton = {
                        if (currentRoute == "home") {
                            FloatingActionButton(onClick = { navController.navigate("order_list") }) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Ke Pesanan")
                            }
                        }
                    }
                ) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        NavHost(navController = navController, startDestination = "home") {
                            composable("home") {
                                // File temanmu dipanggil TANPA perubahan sama sekali
                                HomePageScreen()
                            }
                            composable("order_list") {
                                OrderListScreen(navController = navController)
                            }
                            composable(
                                route = "detail/{orderId}",
                                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val orderId = backStackEntry.arguments?.getString("orderId")
                                OrderDetailScreen(navController = navController, orderId = orderId)
                            }
                        }
                    }
                }
            }
        }
    }
}