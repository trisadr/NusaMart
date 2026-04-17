package com.example.nusamart.model

import com.example.nusamart.R

enum class OrderStatus(val label: String) {
    MENUNGGU("Menunggu"),
    DIPROSES("Diproses"),
    DIKIRIM("Dikirim"),
    SELESAI("Selesai"),
    DIBATALKAN("Dibatalkan")
}

data class Order(
    val id: String,
    val productName: String,
    val productImageRes: Int,
    val sellerName: String,
    val totalPrice: Float,
    val status: OrderStatus,
    val address: String,
    val trackingNumber: String,
    val description: String // <--- SEKARANG SUDAH ADA, ERROR BAKAL HILANG
)

val dummyOrderList = listOf(
    Order(
        id = "ORD-001",
        productName = "Cemilan Emping Jagung Manis Gurih",
        productImageRes = R.drawable.logo,
        sellerName = "Cemal Cemil",
        totalPrice = 35000f,
        status = OrderStatus.SELESAI,
        address = "Malang, Jawa Timur",
        trackingNumber = "RESI123456",
        description = "Cemilan emping jagung renyah dengan paduan rasa manis dan gurih. Dikemas rapi dalam toples dan pouch kedap udara anti melempem sehingga kerenyahannya awet tahan lama"
    ),
    Order(
        id = "ORD-002",
        productName = "Rok Batik Sido Mukti Material Katun Jepang",
        productImageRes = R.drawable.logo,
        sellerName = "Batik Store",
        totalPrice = 100000f,
        status = OrderStatus.DIKIRIM,
        address = "Solo, Jawa Tengah",
        trackingNumber = "RESI987654",
        description = "Batik pilihan khas dari Surakarta dibuat langsung dengan tangan ajaib para pengrajin kain batik yang sudah divaksin sindrom pria solo"
    ),
    Order(
        id = "ORD-003",
        productName = "Tas Anyaman Mini Motif Catur Pink",
        productImageRes = R.drawable.logo,
        sellerName = "Hand Creaft",
        totalPrice = 45000f,
        status = OrderStatus.DIPROSES,
        address = "Balu",
        trackingNumber = "PENDING",
        description = "Tas Anyaman cantik dengan warna pink putih bermotif catur. Dibuat dari bahan kuat dan tahan air. Sangat cocok untuk jalan-jalan santai atau belanja."
    )
)