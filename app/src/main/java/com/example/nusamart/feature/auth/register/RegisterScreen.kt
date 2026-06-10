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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CreditCard
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(vm: RegisterVM = hiltViewModel()) {
    val backStack = LocalBackStack.current
    val context = LocalContext.current
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        vm.successEvent.collect {
            Toast.makeText(context, "Akun berhasil dibuat! Email konfirmasi telah dikirim ke alamatmu. Silakan Login.", Toast.LENGTH_LONG).show()
            backStack.clear()
            backStack.add(Routes.LoginPageRoute)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Daftar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }, enabled = !uiState.isLoading) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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

            Text(
                text = "Buat Akun",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Pilih peranmu dan lengkapi data di bawah ini!",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            // Pilihan Role (Pembeli / Penjual)
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val pembeliBgColor = if (!uiState.isSeller) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
                val pembeliContentColor = if (!uiState.isSeller) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

                OutlinedButton(
                    onClick = { vm.toggleRole(false) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = pembeliBgColor,
                        contentColor = pembeliContentColor
                    )
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pembeli", fontWeight = if (!uiState.isSeller) FontWeight.Bold else FontWeight.Normal)
                }

                val penjualBgColor = if (uiState.isSeller) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
                val penjualContentColor = if (uiState.isSeller) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

                OutlinedButton(
                    onClick = { vm.toggleRole(true) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = penjualBgColor,
                        contentColor = penjualContentColor
                    )
                ) {
                    Icon(Icons.Default.Store, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Penjual", fontWeight = if (uiState.isSeller) FontWeight.Bold else FontWeight.Normal)
                }
            }

            // Input Form Akun Utama
            MyOutlinedTextField(
                value = uiState.username, onValueChange = vm::updateUsername,
                label = "Username", icon = Icons.Default.AccountCircle
            )
            Spacer(modifier = Modifier.height(16.dp))

            MyOutlinedTextField(
                value = uiState.email, onValueChange = vm::updateEmail,
                label = "Email", icon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))

            MyOutlinedTextField(
                value = uiState.phone, onValueChange = vm::updatePhone,
                label = "Nomor Telepon", icon = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))

            MyOutlinedTextField(
                value = uiState.password, onValueChange = vm::updatePassword,
                label = "Password", icon = Icons.Default.Lock,
                isPassword = true, isPasswordVisible = uiState.isPasswordVisible,
                onPasswordVisibilityChange = vm::togglePasswordVisibility
            )
            Spacer(modifier = Modifier.height(16.dp))

            MyOutlinedTextField(
                value = uiState.confirmPassword, onValueChange = vm::updateConfirmPassword,
                label = "Konfirmasi Password", icon = Icons.Default.Lock,
                isPassword = true, isPasswordVisible = uiState.isConfirmPasswordVisible,
                onPasswordVisibilityChange = vm::toggleConfirmPasswordVisibility
            )

            // Form Tambahan Khusus Penjual
            if (uiState.isSeller) {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Data Rekening Pencairan Dana",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(16.dp))

                MyOutlinedTextField(
                    value = uiState.nik, onValueChange = vm::updateNik,
                    label = "NIK (16 Digit)", icon = Icons.Default.Badge,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(16.dp))

                MyOutlinedTextField(
                    value = uiState.bankName, onValueChange = vm::updateBankName,
                    label = "Nama Bank (Cth: BCA, Mandiri)", icon = Icons.Default.AccountBalance
                )
                Spacer(modifier = Modifier.height(16.dp))

                MyOutlinedTextField(
                    value = uiState.accountNumber, onValueChange = vm::updateAccountNumber,
                    label = "Nomor Rekening", icon = Icons.Default.CreditCard,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Button Submit (Buat Akun)
            Button(
                onClick = vm::register,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp), strokeWidth = 3.dp
                    )
                } else {
                    Text("Buat Akun", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Bagian Bawah (Sudah punya akun?)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sudah punya akun?", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                TextButton(
                    onClick = {
                        backStack.clear()
                        backStack.add(Routes.LoginPageRoute)
                    },
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Log In", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Manajemen Dialog
    when (val state = uiState.dialogState) {
        is RegisterDialogState.FormError -> {
            FormErrorDialog(message = state.message, onDismissRequest = vm::clearDialog)
        }
        is RegisterDialogState.PasswordMismatch -> {
            PasswordMismatchDialog(onDismissRequest = vm::clearDialog)
        }
        // <-- TAMBAHAN PEMANGGILAN DIALOG BARU DI SINI -->
        is RegisterDialogState.PasswordTooShort -> {
            PasswordTooShortDialog(onDismissRequest = vm::clearDialog)
        }
        is RegisterDialogState.ApiError -> {
            ApiErrorDialog(
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
        keyboardOptions = keyboardOptions,
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
        confirmButton = {
            TextButton(onClick = onDismissRequest) { Text("OK", color = MaterialTheme.colorScheme.primary) }
        }
    )
}

@Composable
private fun PasswordMismatchDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Password Tidak Cocok", fontWeight = FontWeight.Bold) },
        text = { Text("Password dan konfirmasi password yang kamu masukkan tidak sama. Periksa kembali dan coba lagi.") },
        confirmButton = {
            TextButton(onClick = onDismissRequest) { Text("Perbaiki", color = MaterialTheme.colorScheme.primary) }
        }
    )
}

// <-- TAMBAHAN KOMPONEN DIALOG BARU -->
@Composable
private fun PasswordTooShortDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Password Terlalu Pendek", fontWeight = FontWeight.Bold) },
        text = { Text("Demi keamanan akunmu, password harus terdiri dari minimal 8 karakter. Silakan buat password yang lebih panjang.") },
        confirmButton = {
            TextButton(onClick = onDismissRequest) { Text("Mengerti", color = MaterialTheme.colorScheme.primary) }
        }
    )
}

@Composable
fun ApiErrorDialog(
    message: String,
    onDismissRequest: () -> Unit,
    onLoginClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Registrasi Gagal") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Tutup")
            }
        },
        dismissButton = {
            TextButton(onClick = onLoginClick) {
                Text("Ke Halaman Login")
            }
        }
    )
}