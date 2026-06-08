package com.example.nusamart.feature.buyer.transaction.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.core.Routes
import com.example.nusamart.data.repository.cart.CartRepository
import com.example.nusamart.data.repository.notif.NotificationRepository
import com.example.nusamart.data.repository.order.OrderItemInput
import com.example.nusamart.data.repository.order.OrderRepository
import com.example.nusamart.data.repository.product.ProductRepository
import com.example.nusamart.data.repository.shipping.ShippingRepository
import com.example.nusamart.data.repository.store.StoreRepository
import com.example.nusamart.data.repository.transaction.TransactionRepository
import com.example.nusamart.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutVM @Inject constructor(
    private val orderRepository: OrderRepository,
    private val shippingRepository: ShippingRepository,
    private val transactionRepository: TransactionRepository,
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()

    private var groupedOrderItems = mapOf<String, List<OrderItemInput>>()

    fun loadData(route: Routes.CheckoutRoute) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val userId = userRepository.getActiveUserId()

        // Load Alamat
        val addresses = userRepository.getUserAddresses()
        val address = if (route.selectedAddressId != null) {
            addresses.find { it.idAddress == route.selectedAddressId }
        } else {
            addresses.find { it.isDefault } ?: addresses.firstOrNull()
        }

        // Load Kurir
        var cName = "Pilih Kurir Pengiriman"
        if (route.selectedCourierId != null) {
            val courier = shippingRepository.getCourierById(route.selectedCourierId)
            if (courier != null) cName = "${courier.courierName} (${courier.timeEstimation})"
        }

        // Load Metode Pembayaran
        var pName = "Pilih Metode Pembayaran"
        if (route.selectedPaymentMethodId != null) {
            val methods = transactionRepository.getActivePaymentMethods()
            val method = methods.find { it.idMethod == route.selectedPaymentMethodId }
            if (method != null) pName = method.methodName
        }

        // Inisialisasi Variabel
        val itemsInput = mutableListOf<OrderItemInput>()
        var subTotal = 0.0
        var mainStoreId = "STR-000001"

        // LOGIKA BARANG (DARI KERANJANG ATAU BELI LANGSUNG)
        if (route.fromCart) {
            if (userId != null) {
                val cart = cartRepository.getOrCreateCart(userId)
                val checkedCartItems = cartRepository.getCartItems(cart.idCart).filter { it.isChecked }
                val allProducts = productRepository.getAllProducts()

                val tempGroup = mutableMapOf<String, MutableList<OrderItemInput>>()

                for (cItem in checkedCartItems) {
                    for (product in allProducts) {
                        val pItems = productRepository.getProductItems(product.idProduct)
                        val matchedItem = pItems.find { it.idItem == cItem.idItem }

                        if (matchedItem != null) {
                            val storeId = product.idStore
                            mainStoreId = storeId

                            val variations = productRepository.getProductVariations(matchedItem.idItem)

                            val variantValues = variations.joinToString(", ") { it.value }

                            val displayName = if (variantValues.isNotEmpty()) {
                                "${product.productName} ($variantValues)"
                            } else {
                                product.productName
                            }

                            val orderItem = OrderItemInput(
                                idItem = cItem.idItem,
                                quantity = cItem.quantity,
                                nameSnapshot = displayName,
                                priceSnapshot = matchedItem.price
                            )

                            if (tempGroup.containsKey(storeId)) {
                                tempGroup[storeId]!!.add(orderItem)
                            } else {
                                tempGroup[storeId] = mutableListOf(orderItem)
                            }

                            itemsInput.add(orderItem)
                            subTotal += (matchedItem.price * cItem.quantity)
                            break
                        }
                    }
                }
                groupedOrderItems = tempGroup
            }
        } else if (route.productId != null) {
            val product = productRepository.getAllProducts().find { it.idProduct == route.productId }
            if (product != null) {
                val storeId = product.idStore
                mainStoreId = storeId
                val itemData = productRepository.getProductItems(route.productId).firstOrNull()
                val price = itemData?.price ?: 50000.0

                var displayName = product.productName
                if (itemData != null) {
                    val variations = productRepository.getProductVariations(itemData.idItem)
                    val variantValues = variations.joinToString(", ") { it.value }

                    if (variantValues.isNotEmpty()) {
                        displayName = "${product.productName} ($variantValues)"
                    }
                }

                val orderItem = OrderItemInput(
                    idItem = itemData?.idItem ?: "ITM-0",
                    quantity = route.quantity,
                    nameSnapshot = displayName,
                    priceSnapshot = price
                )

                itemsInput.add(orderItem)
                subTotal = price * route.quantity

                groupedOrderItems = mapOf(storeId to listOf(orderItem))
            }
        }

        // Update UI State
        _uiState.update {
            it.copy(
                isLoading = false,
                address = address,
                courierName = cName,
                paymentName = pName,
                items = itemsInput,
                subtotal = subTotal,
                orderStoreId = mainStoreId
            )
        }
    }

    fun dismissDialog() = _uiState.update { it.copy(showAddressDialog = false) }

    fun placeOrder(route: Routes.CheckoutRoute, onSuccess: (String, String) -> Unit) =
        viewModelScope.launch {
            val state = _uiState.value
            if (state.address == null) {
                _uiState.update { it.copy(showAddressDialog = true) }
                return@launch
            }
            val userId = userRepository.getActiveUserId() ?: return@launch
            _uiState.update { it.copy(isLoading = true) }

            val createdOrderIds = mutableListOf<String>()

            val totalShippingCost = state.shippingCost * groupedOrderItems.size
            val totalPaymentAmount = state.subtotal + state.serviceFee + totalShippingCost

            val payRes = transactionRepository.createPayment(
                userId = userId,
                methodId = route.selectedPaymentMethodId!!,
                totalAmount = totalPaymentAmount,
                imageURL = null
            )

            if (payRes is com.example.nusamart.data.repository.transaction.TransactionResult.Success) {
                val paymentId = payRes.transactionId

                for ((storeId, items) in groupedOrderItems) {
                    val orderRes = orderRepository.createOrder(
                        userId = userId,
                        storeId = storeId,
                        addressId = state.address.idAddress,
                        paymentId = paymentId,
                        items = items,
                        shippingCost = state.shippingCost,
                        servicePrice = 0.0
                    )

                    if (orderRes is com.example.nusamart.data.repository.order.OrderResult.Success) {
                        val orderId = orderRes.orderId
                        createdOrderIds.add(orderId)
                        shippingRepository.createShipping(orderId, route.selectedCourierId!!)

                        // --- TAMBAHAN KODE NOTIFIKASI KE SELLER ---
                        // 1. Cari siapa pemilik tokonya
                        val store = storeRepository.getStoreById(storeId)
                        val sellerId = store?.idSeller

                        // 2. Jika ketemu, gabungkan nama barang dan kirim notifikasi
                        if (sellerId != null) {
                            val productNames = items.joinToString(", ") { it.nameSnapshot }
                            notificationRepository.addNewOrderNotificationForSeller(
                                sellerId = sellerId,
                                orderId = orderId,
                                productNames = productNames
                            )
                        }
                    }
                }

                if (route.fromCart && createdOrderIds.isNotEmpty()) {
                    val cart = cartRepository.getOrCreateCart(userId)
                    val checkedCartItems = cartRepository.getCartItems(cart.idCart).filter { it.isChecked }
                    checkedCartItems.forEach { item ->
                        cartRepository.deleteItem(item.idCartItem)
                    }
                }

                val combinedOrderIds = createdOrderIds.joinToString(", ")
                onSuccess(paymentId, combinedOrderIds)

            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
}