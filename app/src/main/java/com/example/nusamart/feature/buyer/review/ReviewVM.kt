package com.example.nusamart.feature.buyer.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.R
import com.example.nusamart.core.MyApplication
import com.example.nusamart.data.repository.order.OrderRepository
import com.example.nusamart.data.repository.product.ProductRepository
import com.example.nusamart.data.repository.review.ReviewRepository
import com.example.nusamart.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReviewVM(
    private val reviewRepository: ReviewRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                ReviewVM(
                    app.reviewRepository, app.orderRepository,
                    app.productRepository, app.userRepository
                )
            }
        }
    }

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
        val allProducts = productRepository.getAllProducts()
        val forms = unreviewedItems.map { oi ->
            var imageRes = R.drawable.nm_logo
            val productItems = productRepository.getProductItemsByItemId(oi.idItem)
            val product = allProducts.find { it.idProduct == productItems?.idProduct }

            if (product != null) {
                val images = productRepository.getProductImages(product.idProduct)
                val primary = images.find { it.isPrimary }
                if (primary != null) imageRes = primary.imageURL
            }

            ReviewItemForm(
                idOrderItem = oi.idOrderItem,
                productName = oi.nameSnapshot,
                quantity = oi.quantity,
                productImageResId = imageRes
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

        state.itemsToReview.forEach { form ->
            reviewRepository.createReview(
                idOrderItem = form.idOrderItem,
                idUser = userId,
                rating = form.rating.toDouble(),
                comment = form.comment.ifBlank { null },
                imageResId = form.selectedPhotoResId
            )
        }

        _uiState.update { it.copy(isLoading = false, isSubmitSuccess = true) }
    }
}