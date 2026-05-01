package com.example.nusamart.feature.auth

import android.content.Context
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.rememberNavBackStack
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.ui.theme.BlackText
import com.example.nusamart.ui.theme.BluePrimary
import com.example.nusamart.ui.theme.NusaMartTheme
import com.example.nusamart.ui.theme.RedPrimary
import com.example.nusamart.ui.theme.WhiteSurface
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject

// ─── Data helper ─────────────────────────────────────────────────────────────

private fun readBuyerJson(context: Context): JSONArray {
    val internalFile = java.io.File(context.filesDir, "buyer.json")
    return try {
        if (internalFile.exists()) {
            JSONArray(internalFile.readText())
        } else {
            val assetText = context.assets.open("buyer.json").bufferedReader().use { it.readText() }
            internalFile.writeText(assetText)
            JSONArray(assetText)
        }
    } catch (e: Exception) {
        JSONArray()
    }
}

private fun writeBuyerJson(context: Context, array: JSONArray) {
    val internalFile = java.io.File(context.filesDir, "buyer.json")
    internalFile.writeText(array.toString(2))
}

private fun isEmailValid(email: String): Boolean {
    val atIndex = email.indexOf('@')
    if (atIndex <= 0) return false
    if (email.lastIndexOf('@') != atIndex) return false
    val domain = email.substring(atIndex + 1)
    if (domain.isEmpty()) return false
    val dotIndex = domain.lastIndexOf('.')
    if (dotIndex <= 0) return false
    if (dotIndex == domain.length - 1) return false
    return true
}

// ─── Screen ──────────────────────────────────────────────────────────────────

@Composable
fun RegisterScreen() {
    val backStack = LocalBackStack.current
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var konfirmasiPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isSeller by remember { mutableStateOf(false) }

    var isFormErrorDialogOpen by remember { mutableStateOf(false) }    // field kosong & format salah
    var isDuplicateDialogOpen by remember { mutableStateOf(false) }    // username/email sudah dipakai
    var isPasswordMismatchDialogOpen by remember { mutableStateOf(false) } // password tidak cocok

    // Pesan error yang akan ditampilkan di dialog
    var formErrorMessage by remember { mutableStateOf("") }
    var duplicateErrorMessage by remember { mutableStateOf("") }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            delay(2000L)
            isLoading = false
            Toast.makeText(context, "Akun berhasil dibuat! Silakan Login.", Toast.LENGTH_LONG).show()
            backStack.clear()
            backStack.add(Routes.LoginPageRoute)
        }
    }

    Content(
        isLoading = isLoading,
        isSeller = isSeller,
        onSellerChange = { isSeller = it },
        username = username,
        onUsernameChange = { username = it },
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        konfirmasiPassword = konfirmasiPassword,
        onKonfirmasiPasswordChange = { konfirmasiPassword = it },
        isPasswordVisible = isPasswordVisible,
        onPasswordVisibilityChange = { isPasswordVisible = !isPasswordVisible },
        isConfirmPasswordVisible = isConfirmPasswordVisible,
        onConfirmPasswordVisibilityChange = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
        onBackClick = {
            backStack.removeAt(backStack.lastIndex)
        },
        onLoginClick = {
            backStack.clear()
            backStack.add(Routes.LoginPageRoute)
        },
        onRegisterClick = {
            when {
                username.isBlank() -> {
                    formErrorMessage = "Username wajib diisi."
                    isFormErrorDialogOpen = true
                }
                email.isBlank() -> {
                    formErrorMessage = "Email wajib diisi."
                    isFormErrorDialogOpen = true
                }
                password.isBlank() -> {
                    formErrorMessage = "Password wajib diisi."
                    isFormErrorDialogOpen = true
                }
                konfirmasiPassword.isBlank() -> {
                    formErrorMessage = "Konfirmasi password wajib diisi."
                    isFormErrorDialogOpen = true
                }
                !isEmailValid(email.trim()) -> {
                    formErrorMessage = "Format email tidak valid. Pastikan email mengandung '@' dan domain yang benar."
                    isFormErrorDialogOpen = true
                }

                password != konfirmasiPassword -> {
                    isPasswordMismatchDialogOpen = true
                }

                else -> {
                    val buyers = readBuyerJson(context)
                    val usernameLower = username.trim().lowercase()
                    val emailLower = email.trim().lowercase()

                    var usernameTaken = false
                    var emailTaken = false

                    for (i in 0 until buyers.length()) {
                        val obj = buyers.getJSONObject(i)
                        if (obj.optString("username").lowercase() == usernameLower) usernameTaken = true
                        if (obj.optString("email").lowercase() == emailLower) emailTaken = true
                    }

                    when {
                        usernameTaken -> {
                            duplicateErrorMessage = "Username \"${username.trim()}\" sudah digunakan. Silakan pilih username lain."
                            isDuplicateDialogOpen = true
                        }
                        emailTaken -> {
                            duplicateErrorMessage = "Email \"${email.trim()}\" sudah terdaftar. Silakan gunakan email lain atau login."
                            isDuplicateDialogOpen = true
                        }
                        else -> {
                            val newUser = JSONObject().apply {
                                put("email", email.trim())
                                put("username", username.trim())
                                put("password", password)
                                put("address", "")
                                put("profilePicResId", 0)
                                put("role", isSeller)
                            }
                            buyers.put(newUser)
                            writeBuyerJson(context, buyers)

                            isLoading = true
                            isSuccess = true
                        }
                    }
                }
            }
        }
    )

    if (isFormErrorDialogOpen) {
        FormErrorDialog(
            message = formErrorMessage,
            onDismissRequest = { isFormErrorDialogOpen = false }
        )
    }

    if (isPasswordMismatchDialogOpen) {
        PasswordMismatchDialog(
            onDismissRequest = { isPasswordMismatchDialogOpen = false }
        )
    }

    if (isDuplicateDialogOpen) {
        DuplicateAccountDialog(
            message = duplicateErrorMessage,
            onDismissRequest = { isDuplicateDialogOpen = false },
            onLoginClick = {
                isDuplicateDialogOpen = false
                backStack.clear()
                backStack.add(Routes.LoginPageRoute)
            }
        )
    }
}

// ─── Content ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    isLoading: Boolean,
    isSeller: Boolean,
    onSellerChange: (Boolean) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    konfirmasiPassword: String,
    onKonfirmasiPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    isConfirmPasswordVisible: Boolean,
    onConfirmPasswordVisibilityChange: () -> Unit,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Daftar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !isLoading) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = BlackText
                        )
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

            Text(
                text = "Buat Akun",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = BlackText
            )

            Text(
                text = "Pilih peranmu dan lengkapi data di bawah ini!",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { onSellerChange(false) },
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (!isSeller) BluePrimary.copy(alpha = 0.1f) else Color.Transparent,
                        contentColor = if (!isSeller) BluePrimary else Color.Gray
                    )
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pembeli", fontWeight = if (!isSeller) FontWeight.Bold else FontWeight.Normal)
                }

                OutlinedButton(
                    onClick = { onSellerChange(true) },
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSeller) BluePrimary.copy(alpha = 0.1f) else Color.Transparent,
                        contentColor = if (isSeller) BluePrimary else Color.Gray
                    )
                ) {
                    Icon(Icons.Default.Store, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Penjual", fontWeight = if (isSeller) FontWeight.Bold else FontWeight.Normal)
                }
            }

            // Input Form
            MyOutlinedTextField(
                value = username,
                onValueChange = { onUsernameChange(it.trim()) },
                label = "Username",
                icon = Icons.Default.AccountCircle
            )
            Spacer(modifier = Modifier.height(16.dp))

            MyOutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                icon = Icons.Default.Email
            )
            Spacer(modifier = Modifier.height(16.dp))

            MyOutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                icon = Icons.Default.Lock,
                isPassword = true,
                isPasswordVisible = isPasswordVisible,
                onPasswordVisibilityChange = onPasswordVisibilityChange
            )
            Spacer(modifier = Modifier.height(16.dp))

            MyOutlinedTextField(
                value = konfirmasiPassword,
                onValueChange = onKonfirmasiPasswordChange,
                label = "Konfirmasi Password",
                icon = Icons.Default.Lock,
                isPassword = true,
                isPasswordVisible = isConfirmPasswordVisible,
                onPasswordVisibilityChange = onConfirmPasswordVisibilityChange
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onRegisterClick,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedPrimary,
                    contentColor = WhiteSurface,
                    disabledContainerColor = RedPrimary.copy(alpha = 0.6f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = WhiteSurface,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp
                    )
                } else {
                    Text("Buat Akun", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sudah punya akun?", color = Color.Gray, fontSize = 14.sp)
                TextButton(onClick = onLoginClick, enabled = !isLoading) {
                    Text("Log In", color = BluePrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─── Alert Dialogs ────────────────────────────────────────────────────────────

@Composable
private fun FormErrorDialog(
    message: String,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Form Tidak Lengkap", fontWeight = FontWeight.Bold) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("OK")
            }
        }
    )
}

@Composable
private fun PasswordMismatchDialog(
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Password Tidak Cocok", fontWeight = FontWeight.Bold) },
        text = { Text("Password dan konfirmasi password yang kamu masukkan tidak sama. Periksa kembali dan coba lagi.") },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Perbaiki")
            }
        },
    )
}

@Composable
private fun DuplicateAccountDialog(
    message: String,
    onDismissRequest: () -> Unit,
    onLoginClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Akun Sudah Terdaftar", fontWeight = FontWeight.Bold) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onLoginClick) {
                Text("Login", fontWeight = FontWeight.Bold, color = BluePrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Tutup")
            }
        }
    )
}

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenPreview() {
    NusaMartTheme {
        val dummyBackStack = rememberNavBackStack(Routes.RegisterRoute)
        CompositionLocalProvider(LocalBackStack provides dummyBackStack) {
            RegisterScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FormErrorDialogPreview() {
    NusaMartTheme {
        FormErrorDialog(
            message = "Email wajib diisi.",
            onDismissRequest = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PasswordMismatchDialogPreview() {
    NusaMartTheme {
        PasswordMismatchDialog(onDismissRequest = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun DuplicateAccountDialogPreview() {
    NusaMartTheme {
        DuplicateAccountDialog(
            message = "Username \"john123\" sudah digunakan. Silakan pilih username lain.",
            onDismissRequest = {},
            onLoginClick = {}
        )
    }
}