package com.example.nusamart.feature.auth.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nusamart.R
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.ui.theme.BlackText
import com.example.nusamart.ui.theme.NusaMartTheme
import com.example.nusamart.ui.theme.RedPrimary
import com.example.nusamart.ui.theme.WhiteSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(vm: LoginVM = viewModel(factory = LoginVM.Factory)) {
    val backStack = LocalBackStack.current
    val context = LocalContext.current
    val uiState by vm.uiState.collectAsState()

    // listener event sukses dari ViewModel
    LaunchedEffect(Unit) {
        vm.successEvent.collect { role ->
            backStack.clear()
            // Arahkan berdasarkan Role
            if (role == "SELLER") {
                // seller belum diimplementasikan
                //backStack.add(Routes.SellerHomeRoute)
            } else {
                backStack.add(Routes.HomeRoute)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Log In",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackText
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WhiteSurface)
            )
        },
        containerColor = WhiteSurface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.nm_logo),
                contentDescription = "Logo NusaMart",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            // --- Form Input ---
            MyOutlinedTextField(
                value = uiState.emailOrUsername,
                onValueChange = vm::updateEmailOrUsername,
                label = "Email/Username",
                icon = Icons.Default.Person
            )
            Spacer(modifier = Modifier.height(16.dp))
            MyOutlinedTextField(
                value = uiState.password,
                onValueChange = vm::updatePassword,
                label = "Password",
                icon = Icons.Default.Lock,
                isPassword = true,
                isPasswordVisible = uiState.isPasswordVisible,
                onPasswordVisibilityChange = vm::togglePasswordVisibility
            )
            Spacer(modifier = Modifier.height(32.dp))
            // --- Tombol Submit ---
            Button(
                onClick = vm::login,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedPrimary,
                    contentColor = WhiteSurface,
                    disabledContainerColor = RedPrimary.copy(alpha = 0.6f)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = WhiteSurface, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                } else {
                    Text("Log In", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // --- Aksi Ekstra ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { backStack.add(Routes.RegisterRoute) }, enabled = !uiState.isLoading) {
                    Text("Daftar")
                }
                TextButton(
                    onClick = { Toast.makeText(context, "Fitur lupa password belum tersedia", Toast.LENGTH_SHORT).show() },
                    enabled = !uiState.isLoading
                ) {
                    Text("Lupa Password?")
                }
            }
        }
    }
    // --- Manajemen Alert Dialog ---
    when (val state = uiState.dialogState) {
        is LoginDialogState.Error -> {
            LoginErrorDialog(
                title = state.title,
                message = state.message,
                onDismiss = vm::clearDialog
            )
        }
        LoginDialogState.None -> { /* Tidak ada dialog */ }
    }
}


// Komponen Pendukung

@Composable
fun MyOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onPasswordVisibilityChange: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        trailingIcon = {
            if (isPassword) {
                val image = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = onPasswordVisibilityChange) {
                    Icon(image, contentDescription = "Toggle password visibility")
                }
            }
        },
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
private fun LoginErrorDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK", fontWeight = FontWeight.Bold)
            }
        }
    )
}

// ─── Preview ───
@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    NusaMartTheme {
        LoginScreen()
    }
}