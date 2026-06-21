package com.example.nusamart.feature.landingpage

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nusamart.R
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.ui.theme.NusaMartTheme

@Composable
fun LandingScreen() {
    val context = LocalContext.current
    val backStack = LocalBackStack.current

    val viewModel: LandingViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.navigateTo) {
        uiState.navigateTo?.let { route ->
            backStack.clear()
            backStack.add(route)
            viewModel.onNavigated() // reset agar tidak navigate ulang
        }
    }

    LandingContent(uiState = uiState)
}

@Composable
private fun LandingContent(uiState: LandingUiState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
                    .size(40.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LandingScreenPreview() {
    NusaMartTheme {
        LandingContent(uiState = LandingUiState(isLoading = true))
    }
}