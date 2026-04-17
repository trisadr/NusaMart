package com.example.nusamart.feature.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nusamart.R
import com.example.nusamart.ui.theme.RedPrimary
import com.example.nusamart.ui.theme.WhiteSurface

@Preview
@Composable
fun LandingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteSurface)
    ) {
        // Logo + nama
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // logo
            Image(
                painter = painterResource(id = R.drawable.nm_logo),
                contentDescription = "Logo NusaMart",
                modifier = Modifier.size(250.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

//            // Konten nama
//            Text(
//                text = "NusaMart",
//                fontSize = 32.sp,
//                fontWeight = FontWeight.Bold,
//                color = RedPrimary,
//                letterSpacing = 2.sp // jarak antar huruf
//            )
        }

        // Menunjukkan bahwa aplikasi sedang bersiap
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp) // Jarak dari ujung bawah HP
                .size(40.dp),
            color = RedPrimary,
            strokeWidth = 4.dp
        )
    }
}