package com.example.nusamart.feature.buyer.profile.address

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nusamart.core.LocalBackStack
import com.example.nusamart.data.model.user.UserAddressJson
import com.example.nusamart.ui.theme.BlackText
import com.example.nusamart.ui.theme.BluePrimary
import com.example.nusamart.ui.theme.GrayBackground
import com.example.nusamart.ui.theme.RedPrimary
import com.example.nusamart.ui.theme.WhiteSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(vm: AddressVM = viewModel(factory = AddressVM.Factory)) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    val topBarTitle = when {
        !uiState.isFormVisible -> "Alamat Pengiriman"
        uiState.editAddressId != null -> "Edit Alamat"
        else -> "Tambah Alamat"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BlackText) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.isFormVisible) vm.hideForm() else backStack.removeAt(backStack.lastIndex)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = BlackText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WhiteSurface)
            )
        },
        floatingActionButton = {
            if (!uiState.isFormVisible) {
                FloatingActionButton(onClick = vm::showAddForm, containerColor = BluePrimary, contentColor = Color.White) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Alamat")
                }
            }
        },
        containerColor = GrayBackground
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.isFormVisible) {
                AddOrEditAddressForm(uiState = uiState, vm = vm)
            } else {
                AddressList(
                    addresses = uiState.addresses,
                    onEdit = vm::showEditForm,
                    onDelete = vm::deleteAddress
                )
            }
        }
    }
}

@Composable
private fun AddressList(
    addresses: List<UserAddressJson>,
    onEdit: (UserAddressJson) -> Unit,
    onDelete: (String) -> Unit
) {
    if (addresses.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada alamat tersimpan.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(addresses) { address ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = address.label, fontWeight = FontWeight.Bold, color = BluePrimary)
                                if (address.isDefault) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = RedPrimary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("Utama", color = RedPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = address.receiver, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Text(text = address.phone, color = Color.Gray, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = address.completeAddress, fontSize = 14.sp, lineHeight = 20.sp)
                            Text(text = "${address.city}, ${address.province} ${address.postalCode}", fontSize = 14.sp, color = Color.DarkGray)
                        }

                        // Tombol Aksi (Edit & Delete)
                        Column(horizontalAlignment = Alignment.End) {
                            IconButton(onClick = { onEdit(address) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                            }
                            IconButton(onClick = { onDelete(address.idAddress) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = RedPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddOrEditAddressForm(uiState: AddressUiState, vm: AddressVM) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteSurface)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = uiState.formLabel, onValueChange = vm::updateLabel,
            label = { Text("Label (Misal: Rumah, Kantor)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.formReceiver, onValueChange = vm::updateReceiver,
            label = { Text("Nama Penerima") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.formPhone, onValueChange = vm::updatePhone,
            label = { Text("Nomor Telepon") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = uiState.formCity, onValueChange = vm::updateCity,
                label = { Text("Kota/Kabupaten") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = uiState.formProvince, onValueChange = vm::updateProvince,
                label = { Text("Provinsi") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.formPostalCode, onValueChange = vm::updatePostalCode,
            label = { Text("Kode Pos") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.formCompleteAddress, onValueChange = vm::updateCompleteAddress,
            label = { Text("Alamat Lengkap (Nama Jalan, RT/RW, Patokan)") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            maxLines = 4
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = uiState.formIsDefault,
                onCheckedChange = vm::updateIsDefault,
                colors = CheckboxDefaults.colors(checkedColor = BluePrimary)
            )
            Text("Jadikan sebagai Alamat Utama", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = vm::saveAddress,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) {
            Text("Simpan Alamat", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}