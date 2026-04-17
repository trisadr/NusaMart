package com.example.nusamart.feature.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nusamart.R
import com.example.nusamart.ui.theme.BlackText
import com.example.nusamart.ui.theme.BluePrimary
import com.example.nusamart.ui.theme.RedPrimary
import com.example.nusamart.ui.theme.WhiteSurface

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBackClick: () -> Unit = {}
) {
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

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
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali ke beranda",
                            tint = BlackText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteSurface
                )
            )
        },
        containerColor = WhiteSurface
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Logo NM
                Image(
                    painter = painterResource(id = R.drawable.nm_logo),
                    contentDescription = "Logo NusaMart",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Input
                MyOutlinedTextField(
                    value = emailOrUsername,
                    onValueChange = { emailOrUsername = it },
                    label = "No. Handphone/Email/Username",
                    icon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(16.dp))

                MyOutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    icon = Icons.Default.Lock,
                    isPassword = true,
                    isPasswordVisible = isPasswordVisible,
                    onPasswordVisibilityChange = { isPasswordVisible = !isPasswordVisible }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Login
                Button(
                    onClick = { /* Aksi login di sini */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedPrimary,
                        contentColor = WhiteSurface
                    )
                ) {
                    Text(
                        text = "Log In",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Daftar n lupa pw
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { /* Navigasi ke Daftar */ }) {
                        Text("Daftar", color = BluePrimary, fontSize = 14.sp)
                    }
                    TextButton(onClick = { /* Navigasi ke Lupa Password */ }) {
                        Text("Lupa Password?", color = BluePrimary, fontSize = 14.sp)
                    }
                }

                // Atau
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray
                    )
                    Text(
                        text = " atau ",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray
                    )
                }

                // Login google
                OutlinedButton(
                    onClick = { /* Aksi login Google di sini */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.LightGray),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BlackText
                    )
                ) {
                    // Logo google
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Logo Google",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Log In dengan Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
        leadingIcon = { Icon(imageVector = icon, contentDescription = null, tint = Color.Gray) },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = onPasswordVisibilityChange) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        },
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = BlackText,
            unfocusedTextColor = BlackText,
            focusedBorderColor = RedPrimary,
            unfocusedBorderColor = Color.LightGray,
            focusedLabelColor = RedPrimary,
            unfocusedLabelColor = Color.Gray,
            cursorColor = RedPrimary
        )
    )
}