package com.example.nusamart.feature.auth.register

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.ui.theme.BlackText
import com.example.nusamart.ui.theme.BluePrimary
import com.example.nusamart.ui.theme.RedPrimary
import com.example.nusamart.ui.theme.WhiteSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(vm: RegisterVM = viewModel(factory = RegisterVM.Factory)) {
    val backStack = LocalBackStack.current
    val context = LocalContext.current
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        vm.successEvent.collect {
            Toast.makeText(context, "Akun berhasil dibuat! Silakan Login.", Toast.LENGTH_LONG).show()
            backStack.clear()
            backStack.add(Routes.LoginPageRoute)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BlackText) },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }, enabled = !uiState.isLoading) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = BlackText)
                    }
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text("Buat Akun", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = BlackText)
            Text(
                "Pilih peranmu dan lengkapi data di bawah ini!",
                fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            // Pilihan Role
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { vm.toggleRole(false) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (!uiState.isSeller) BluePrimary.copy(alpha = 0.1f) else Color.Transparent,
                        contentColor = if (!uiState.isSeller) BluePrimary else Color.Gray
                    )
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pembeli", fontWeight = if (!uiState.isSeller) FontWeight.Bold else FontWeight.Normal)
                }

                OutlinedButton(
                    onClick = { vm.toggleRole(true) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (uiState.isSeller) BluePrimary.copy(alpha = 0.1f) else Color.Transparent,
                        contentColor = if (uiState.isSeller) BluePrimary else Color.Gray
                    )
                ) {
                    Icon(Icons.Default.Store, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Penjual", fontWeight = if (uiState.isSeller) FontWeight.Bold else FontWeight.Normal)
                }
            }

            // Input Form
            MyOutlinedTextField(
                value = uiState.username,
                onValueChange = vm::updateUsername,
                label = "Username", icon = Icons.Default.AccountCircle
            )
            Spacer(modifier = Modifier.height(16.dp))

            MyOutlinedTextField(
                value = uiState.email,
                onValueChange = vm::updateEmail,
                label = "Email", icon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // keyboard angka untuk nomor telepon
            MyOutlinedTextField(
                value = uiState.phone,
                onValueChange = vm::updatePhone,
                label = "Nomor Telepon", icon = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))

            MyOutlinedTextField(
                value = uiState.password,
                onValueChange = vm::updatePassword,
                label = "Password", icon = Icons.Default.Lock,
                isPassword = true,
                isPasswordVisible = uiState.isPasswordVisible,
                onPasswordVisibilityChange = vm::togglePasswordVisibility
            )
            Spacer(modifier = Modifier.height(16.dp))

            MyOutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = vm::updateConfirmPassword,
                label = "Konfirmasi Password", icon = Icons.Default.Lock,
                isPassword = true,
                isPasswordVisible = uiState.isConfirmPasswordVisible,
                onPasswordVisibilityChange = vm::toggleConfirmPasswordVisibility
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Button Submit
            Button(
                onClick = vm::register,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
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
                    Text("Buat Akun", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sudah punya akun?", color = Color.Gray, fontSize = 14.sp)
                TextButton(onClick = {
                    backStack.clear()
                    backStack.add(Routes.LoginPageRoute)
                }, enabled = !uiState.isLoading) {
                    Text("Log In", color = BluePrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    when (val state = uiState.dialogState) {
        is RegisterDialogState.FormError -> {
            FormErrorDialog(message = state.message, onDismissRequest = vm::clearDialog)
        }
        is RegisterDialogState.PasswordMismatch -> {
            PasswordMismatchDialog(onDismissRequest = vm::clearDialog)
        }
        is RegisterDialogState.DuplicateAccount -> {
            DuplicateAccountDialog(
                message = state.message,
                onDismissRequest = vm::clearDialog,
                onLoginClick = {
                    vm.clearDialog()
                    backStack.clear()
                    backStack.add(Routes.LoginPageRoute)
                }
            )
        }
        RegisterDialogState.None -> { }
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onPasswordVisibilityChange: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        trailingIcon = {
            if (isPassword) {
                val image = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = onPasswordVisibilityChange) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility")
                }
            }
        },
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = keyboardOptions, // Pasang parameter keyboardOptions di sini
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
private fun FormErrorDialog(message: String, onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Form Tidak Lengkap", fontWeight = FontWeight.Bold) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onDismissRequest) { Text("OK") } }
    )
}

@Composable
private fun PasswordMismatchDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Password Tidak Cocok", fontWeight = FontWeight.Bold) },
        text = { Text("Password dan konfirmasi password yang kamu masukkan tidak sama. Periksa kembali dan coba lagi.") },
        confirmButton = { TextButton(onClick = onDismissRequest) { Text("Perbaiki") } }
    )
}

@Composable
private fun DuplicateAccountDialog(message: String, onDismissRequest: () -> Unit, onLoginClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Akun Sudah Terdaftar", fontWeight = FontWeight.Bold) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onLoginClick) { Text("Login", fontWeight = FontWeight.Bold, color = BluePrimary) } },
        dismissButton = { TextButton(onClick = onDismissRequest) { Text("Tutup") } }
    )
}