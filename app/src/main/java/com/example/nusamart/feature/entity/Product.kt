package com.example.nusamart.feature.entity

import androidx.compose.ui.graphics.Path
import com.example.nusamart.R

class Product (
    var id: Int,
    var name: String,
    var price: Float,
    var location: String,
    var imageRes: Int,
    var description: String,
    var map: String
)

val dummyProductList = listOf(
    Product(
        id = 1,
        name = "Paper Cup Kopi Aesthetic (Isi 50)",
        price = 25000f,
        location = "Jakarta",
        imageRes = R.drawable.cup,
        description = "Gelas kertas tebal dengan penutup hitam elegan. Cocok untuk menyajikan minuman panas maupun dingin segar. Material aman (food grade) dan tidak mudah bocor. Kalo bocor ya maaf",
        map = "https://maps.app.goo.gl/TaYArGwxrpCBy1EN9"
    ),
    Product(
        id = 2,
        name = "Cemilan Emping Jagung Manis Gurih",
        price = 35000f,
        location = "Malang",
        imageRes = R.drawable.emping,
        description = "Cemilan emping jagung renyah dengan paduan rasa manis dan gurih. Dikemas rapi dalam toples dan pouch kedap udara anti melempem sehingga kerenyahannya awet tahan lama.",
        map = "https://maps.app.goo.gl/TaYArGwxrpCBy1EN9"
    ),
    Product(
        id = 3,
        name = "Keranjang Rotan Estetik Serbaguna",
        price = 75000f,
        location = "Yogyakarta",
        imageRes = R.drawable.keranjang,
        description = "Keranjang anyaman rotan asli kualitas ekspor. Sangat kuat namun ringan. Cocok digunakan sebagai tempat buah, properti piknik, maupun dekorasi ruangan.",
        map = "https://maps.app.goo.gl/TaYArGwxrpCBy1EN9"
    ),
    Product(
        id = 4,
        name = "Keranjang/Tas Anyaman Mini Motif Catur Pink",
        price = 45000f,
        location = "Bali",
        imageRes = R.drawable.tas,
        description = "Tas anyaman cantik dengan warna pink putih bermotif catur. Dibuat dari bahan kuat dan tahan air. Sangat cocok untuk jalan-jalan santai atau belanja.",
        map = "https://maps.app.goo.gl/TaYArGwxrpCBy1EN9"
    ),
    Product(
        id = 5,
        name = "Telur Ayam Negeri Segar (1 Kg)",
        price = 28000f,
        location = "Bogor",
        imageRes = R.drawable.telur,
        description = "Telur ayam negeri pilihan kualitas premium. Dikirim langsung dari peternakan oleh ayamnya langsung yang baru bertelur setiap pagi sehingga dijamin segar, kuning telur utuh, dan kaya protein.",
        map = "https://maps.app.goo.gl/TaYArGwxrpCBy1EN9"
    ),
    Product(
        id = 6,
        name = "Rok Batik Sido Mukti Material Katun Jepang",
        price = 100000f,
        location = "Solo",
        imageRes = R.drawable.rok,
        description = "Batik pilihan khas dari Surakarta dibuat langsung dengan tangan ajaib para pengrajin kain batik yang sudah divaksin sindrom pria solo",
        map = "https://maps.app.goo.gl/TaYArGwxrpCBy1EN9"
    )
)