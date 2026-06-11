package com.example.nusamart.feature.chat.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.nusamart.R
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.data.dto.ChatMessageDto
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(roomId: String, vm: ChatDetailVM = hiltViewModel()) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()
    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // 1. Muat data chat saat layar pertama kali dibuka
    LaunchedEffect(roomId) {
        vm.loadChat(roomId)
    }

    // 2. Auto-scroll ke pesan paling bawah saat ada pesan baru
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Foto Profil Lawan Bicara
                        AsyncImage(
                            model = uiState.otherUserImageUrl,
                            contentDescription = "Foto Profil",
                            placeholder = painterResource(id = R.drawable.nm_logo),
                            error = painterResource(id = R.drawable.nm_logo),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Nama Lawan Bicara
                        Column {
                            Text(
                                text = uiState.otherUserName.ifBlank { "Pengguna" },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            // Area Input Pesan (menggunakan imePadding agar tidak tertutup keyboard)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ketik pesan...", color = Color.Gray) },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            // Opsional: Jika kamu ingin batas pinggirnya (border) transparan juga agar lebih estetik
                            // focusedBorderColor = Color.Transparent,
                            // unfocusedBorderColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Tombol Kirim
                    IconButton(
                        onClick = {
                            val toSend = text.trim()
                            if (toSend.isNotBlank()) {
                                text = "" // Kosongkan input setelah dikirim
                                vm.sendMessage(toSend)
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFFF6D00), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Kirim",
                            tint = Color.White,
                            modifier = Modifier.padding(start = 4.dp) // Geser icon send sedikit ke kanan agar seimbang
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFF6D00))
            }
        } else {
            // Mengelompokkan pesan berdasarkan tanggal (Parsing String ke LocalDate)
            val groupedMessages = uiState.messages
                .groupBy { msg ->
                    try {
                        Instant.parse(msg.createAt).atZone(ZoneId.systemDefault()).toLocalDate()
                    } catch (e: Exception) {
                        LocalDate.now()
                    }
                }
                .toSortedMap()

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                groupedMessages.forEach { (date, messages) ->
                    // 1. Header Tanggal
                    item(key = "date_${date}") {
                        DateLabel(date = date)
                    }

                    // 2. Daftar Pesan di hari tersebut
                    items(messages, key = { it.idChat }) { msg ->
                        val isMe = msg.senderId == uiState.currentUserId
                        ChatBubble(msg = msg, isMe = isMe)
                    }
                }

                // Ruang kosong di bawah agar pesan terakhir tidak terlalu mepet
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun DateLabel(date: LocalDate) {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    val label = when (date) {
        today -> "Hari ini"
        yesterday -> "Kemarin"
        else -> date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ChatBubble(msg: ChatMessageDto, isMe: Boolean) {
    // Membaca waktu dari string ISO-8601 Laravel
    val timeText = try {
        val localTime = Instant.parse(msg.createAt).atZone(ZoneId.systemDefault()).toLocalDateTime()
        localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        "--:--"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isMe) Color(0xFFFF6D00) else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp, // Membuat ekor lancip di kiri bawah jika orang lain
                bottomEnd = if (isMe) 4.dp else 16.dp   // Membuat ekor lancip di kanan bawah jika kita
            ),
            modifier = Modifier.widthIn(min = 80.dp, max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 6.dp)) {

                // Teks Pesan
                Text(
                    text = msg.messageText,
                    color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Baris Waktu dan Status Baca
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.End) // Selalu dorong waktu ke kanan bawah bubble
                ) {
                    Text(
                        text = timeText,
                        fontSize = 10.sp,
                        color = if (isMe) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )

                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        // Cek status baca menggunakan angka 1 (karena DTO menggunakan Int)
                        Text(
                            text = if (msg.isRead == 1) "✓✓" else "✓",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (msg.isRead == 1) Color(0xFF4FC3F7) else Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}