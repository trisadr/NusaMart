package com.example.nusamart.feature.buyer.review

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.data.repository.order.OrderRepository
import com.example.nusamart.data.repository.product.ProductRepository
import com.example.nusamart.data.repository.product.ProductResult
import com.example.nusamart.data.repository.review.ReviewRepository
import com.example.nusamart.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ReviewVM @Inject constructor(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState = _uiState.asStateFlow()

    fun loadOrderItems(orderId: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, orderId = orderId) }

        // 1. Cek status order, pastikan DELIVERED
        val order = orderRepository.getOrderById(orderId)
        if (order?.orderStatus != "DELIVERED") {
            _uiState.update { it.copy(isLoading = false, isOrderDelivered = false) }
            return@launch
        }

        // 2. Ambil Order Items dan Cek Ulasan yang sudah ada
        val orderItems = orderRepository.getOrderItems(orderId)
        val itemIds = orderItems.map { it.idOrderItem }
        val existingReviews = reviewRepository.getReviewsByItemIds(itemIds)
        val reviewedItemIds = existingReviews.map { it.idOrderItem }.toSet()

        // 3. Filter hanya item yang BELUM diulas
        val unreviewedItems = orderItems.filter { it.idOrderItem !in reviewedItemIds }

        // Jika semua sudah diulas, set flag allReviewed ke true
        if (unreviewedItems.isEmpty()) {
            _uiState.update {
                it.copy(isLoading = false, isOrderDelivered = true, allReviewed = true, itemsToReview = emptyList())
            }
            return@launch
        }

        // 4. Proses data untuk UI
        // Buka bungkus ProductResult
        val productsResult = productRepository.getAllProducts()
        val allProducts = if (productsResult is ProductResult.Success) productsResult.data else emptyList()

        // Buat map untuk mencocokkan idItem dengan imageURL utama produk
        val itemImageMap = mutableMapOf<String, String>()

        allProducts.forEach { product ->
            val detailResult = productRepository.getProductDetail(product.idProduct)
            if (detailResult is ProductResult.Success) {
                val detailData = detailResult.data
                val primaryImage = detailData.images.find { it.isPrimary == 1 }?.imageURL

                if (primaryImage != null) {
                    // Simpan URL gambar untuk setiap variasi item di produk ini
                    detailData.items.forEach { item ->
                        itemImageMap[item.idItem] = primaryImage
                    }
                }
            }
        }

        // Petakan ke ReviewItemForm
        val forms = unreviewedItems.map { oi ->
            ReviewItemForm(
                idOrderItem = oi.idOrderItem,
                productName = oi.nameSnapshot,
                quantity = oi.quantity,
                productImageUrl = itemImageMap[oi.idItem]
            )
        }

        _uiState.update {
            it.copy(isLoading = false, isOrderDelivered = true, allReviewed = false, itemsToReview = forms)
        }
    }

    fun updateRating(idOrderItem: String, rating: Int) {
        _uiState.update { state ->
            val newItems = state.itemsToReview.map {
                if (it.idOrderItem == idOrderItem) it.copy(rating = rating) else it
            }
            state.copy(itemsToReview = newItems, showValidationError = false)
        }
    }

    fun updateComment(idOrderItem: String, text: String) {
        _uiState.update { state ->
            val newItems = state.itemsToReview.map {
                if (it.idOrderItem == idOrderItem) it.copy(comment = text) else it
            }
            state.copy(itemsToReview = newItems)
        }
    }

    fun submitReviews() = viewModelScope.launch {
        val state = _uiState.value
        val userId = userRepository.getActiveUserId()

        if (userId == null) return@launch

        val hasUnratedItem = state.itemsToReview.any { it.rating == 0 }
        if (hasUnratedItem) {
            _uiState.update { it.copy(showValidationError = true) }
            return@launch
        }

        _uiState.update { it.copy(isLoading = true) }

        // Loop untuk mengirim semua ulasan
        state.itemsToReview.forEach { form ->

            // PERBAIKAN DI SINI:
            // Hapus idUser dan ubah imageUrl menjadi localImagePath
            reviewRepository.createReview(
                idOrderItem = form.idOrderItem,
                rating = form.rating.toDouble(),
                comment = form.comment.ifBlank { null },
                localImagePath = form.selectedPhoto
            )
        }

        _uiState.update { it.copy(isLoading = false, isSubmitSuccess = true) }
    }

    // FUNGSI BARU: Konversi Uri ke File Path dan update State
    fun updatePhoto(context: Context, idOrderItem: String, uri: Uri) {
        viewModelScope.launch {
            try {
                // 1. Baca data dari Uri
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@launch

                // 2. Buat file sementara di cache direktori
                val tempFile = File.createTempFile("review_img_", ".jpg", context.cacheDir)
                tempFile.outputStream().use { output ->
                    inputStream.copyTo(output)
                }

                // 3. Simpan absolute path-nya ke UI State
                val localPath = tempFile.absolutePath

                _uiState.update { state ->
                    val newItems = state.itemsToReview.map {
                        if (it.idOrderItem == idOrderItem) it.copy(selectedPhoto = localPath) else it
                    }
                    state.copy(itemsToReview = newItems)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Abaikan jika gagal baca gambar
            }
        }
    }
}