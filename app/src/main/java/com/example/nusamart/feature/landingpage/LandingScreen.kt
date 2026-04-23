package com.example.nusamart.feature.landingpage // Pastikan package-nya sesuai

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nusamart.R
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.ui.theme.NusaMartTheme
import com.example.nusamart.ui.theme.RedPrimary
import com.example.nusamart.ui.theme.WhiteSurface
import kotlinx.coroutines.delay

@Composable
fun LandingScreen() {
    val backStack = LocalBackStack.current

    LaunchedEffect(Unit) {
        delay(3000L)
        backStack.clear()
        backStack.add(Routes.LoginPageRoute)
    }

    Content()
}

@Composable
private fun Content() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteSurface)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.nm_logo),
                contentDescription = "Logo NusaMart",
                modifier = Modifier.size(250.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .size(40.dp),
            color = RedPrimary,
            strokeWidth = 4.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LandingPagePreview() {
    NusaMartTheme() {
        Content()
    }
}