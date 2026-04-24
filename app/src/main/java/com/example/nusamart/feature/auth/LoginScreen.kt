package com.example.nusamart.feature.auth

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nusamart.R
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.core.Routes
import com.example.nusamart.core.activeUser
import com.example.nusamart.entity.Buyer
import com.example.nusamart.ui.theme.BlackText
import com.example.nusamart.ui.theme.NusaMartTheme
import com.example.nusamart.ui.theme.RedPrimary
import com.example.nusamart.ui.theme.WhiteSurface
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun loadBuyers(context: Context): List<Buyer> {
    return try {
        val jsonString = context.assets.open("buyer.json")
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<List<Buyer>>() {}.type
        Gson().fromJson(jsonString, type) ?: emptyList()

    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

@Composable
fun LoginScreen() {
    val backStack = LocalBackStack.current
    val context = LocalContext.current

    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Content(
        emailOrUsername = emailOrUsername,
        onEmailOrUsernameChange = { emailOrUsername = it },
        password = password,
        onPasswordChange = { password = it },
        isPasswordVisible = isPasswordVisible,
        onPasswordVisibilityChange = { isPasswordVisible = !isPasswordVisible },

        onLoginClick = {
            if (emailOrUsername.isBlank() || password.isBlank()) {
                Toast.makeText(
                    context,
                    "Email/Username dan Password wajib diisi",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val buyers = loadBuyers(context)
                if (buyers.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Data buyer tidak ditemukan",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val matchedBuyer = buyers.find {
                        (it.email == emailOrUsername || it.username == emailOrUsername)
                                && it.password == password
                    }
                    if (matchedBuyer != null) {
                        activeUser = matchedBuyer
                        backStack.clear()
                        backStack.add(Routes.HomeRoute)
                    } else {
                        Toast.makeText(
                            context,
                            "Login gagal! Username/email atau password salah",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        },

        onRegisterClick = {
            backStack.add(Routes.RegisterRoute)
        },

        onForgotPasswordClick = {
            Toast.makeText(context, "Fitur lupa password belum tersedia", Toast.LENGTH_SHORT).show()
        },

        onGoogleLoginClick = {
            signInWithGoogle(context)
        },

        onBackClick = {
            backStack.removeAt(backStack.lastIndex)
        }
    )
}

fun signInWithGoogle(context: Context) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    val signInIntent: Intent = googleSignInClient.signInIntent
    context.startActivity(signInIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    emailOrUsername: String,
    onEmailOrUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onBackClick: () -> Unit
) {
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
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, null, tint = BlackText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteSurface
                )
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
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            MyOutlinedTextField(
                value = emailOrUsername,
                onValueChange = onEmailOrUsernameChange,
                label = "Email/Username",
                icon = Icons.Default.Person
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

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
            ) {
                Text("Log In")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onRegisterClick) {
                    Text("Daftar")
                }

                TextButton(onClick = onForgotPasswordClick) {
                    Text("Lupa Password?")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onGoogleLoginClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log In dengan Google")
            }
        }
    }
}

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
        leadingIcon = { Icon(icon, null) },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = onPasswordVisibilityChange) {
                    Icon(
                        if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        null
                    )
                }
            }
        },
        visualTransformation = if (isPassword && !isPasswordVisible)
            PasswordVisualTransformation()
        else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth()
    )
}

// ==========================================
// PREVIEWS
// ==========================================

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    NusaMartTheme() {
        Content(
            emailOrUsername = "",
            onEmailOrUsernameChange = {},
            password = "",
            onPasswordChange = {},
            isPasswordVisible = false,
            onPasswordVisibilityChange = {},
            onLoginClick = {},
            onRegisterClick = {},
            onForgotPasswordClick = {},
            onGoogleLoginClick = {},
            onBackClick = {}
        )
    }
}