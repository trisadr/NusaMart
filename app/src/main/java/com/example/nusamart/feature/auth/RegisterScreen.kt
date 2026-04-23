//package com.example.nusamart.feature.screen
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Email
//import androidx.compose.material.icons.filled.Lock
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.nusamart.feature.auth.MyOutlinedTextField
//import com.example.nusamart.ui.theme.BlackText
//import com.example.nusamart.ui.theme.BluePrimary
//import com.example.nusamart.ui.theme.RedPrimary
//import com.example.nusamart.ui.theme.WhiteSurface
//
//@Preview
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun RegisterScreen(
//    onBackClick: () -> Unit = {},
//    onLoginClick: () -> Unit = {} // User dah punya akun
//) {
//    var namaLengkap by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var konfirmasiPassword by remember { mutableStateOf("") }
//    var isPasswordVisible by remember { mutableStateOf(false) }
//    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = "Daftar",
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = BlackText
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Kembali",
//                            tint = BlackText
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = WhiteSurface
//                )
//            )
//        },
//        containerColor = WhiteSurface
//    ) { paddingValues ->
//
//        // Modifier.verticalScroll biar layar bisa digeser ke bawah pas keyboard muncul
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(horizontal = 24.dp)
//                .verticalScroll(rememberScrollState()),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Text(
//                text = "Buat Akun",
//                fontSize = 30.sp,
//                fontWeight = FontWeight.Bold,
//                color = BlackText
//            )
//
//            Text(
//                text = "Lengkapi data di bawah ini untuk mulai menjelajahi produk lokal terbaik di NusaMart!",
//                fontSize = 13.sp,
//                color = Color.Gray,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
//            )
//
//            MyOutlinedTextField(
//                value = namaLengkap,
//                onValueChange = { namaLengkap = it },
//                label = "Nama Lengkap",
//                icon = Icons.Default.Person
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            MyOutlinedTextField(
//                value = email,
//                onValueChange = { email = it },
//                label = "Email",
//                icon = Icons.Default.Email // Pakai icon amplop untuk email
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            MyOutlinedTextField(
//                value = password,
//                onValueChange = { password = it },
//                label = "Password",
//                icon = Icons.Default.Lock,
//                isPassword = true,
//                isPasswordVisible = isPasswordVisible,
//                onPasswordVisibilityChange = { isPasswordVisible = !isPasswordVisible }
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            MyOutlinedTextField(
//                value = konfirmasiPassword,
//                onValueChange = { konfirmasiPassword = it },
//                label = "Konfirmasi Password",
//                icon = Icons.Default.Lock,
//                isPassword = true,
//                isPasswordVisible = isConfirmPasswordVisible,
//                onPasswordVisibilityChange = {
//                    isConfirmPasswordVisible = !isConfirmPasswordVisible
//                }
//            )
//
//            Spacer(modifier = Modifier.height(40.dp))
//
//            Button(
//                onClick = { /* Aksi simpan data / register di sini */ },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp),
//                shape = RoundedCornerShape(8.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = RedPrimary,
//                    contentColor = WhiteSurface
//                )
//            ) {
//                Text(
//                    text = "Buat Akun",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 16.dp, bottom = 32.dp),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(text = "Sudah punya akun?", color = Color.Gray, fontSize = 14.sp)
//                TextButton(onClick = onLoginClick) {
//                    Text("Log In", color = BluePrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
//                }
//            }
//        }
//    }
//}