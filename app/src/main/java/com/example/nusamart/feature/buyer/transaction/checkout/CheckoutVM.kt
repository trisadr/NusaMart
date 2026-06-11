package com.example.nusamart.feature.buyer.transaction.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nusamart.core.Routes
import com.example.nusamart.data.repository.cart.CartRepository
import com.example.nusamart.data.repository.notif.NotificationRepository
import com.example.nusamart.data.repository.order.OrderItemInput
import com.example.nusamart.data.repository.order.OrderRepository
import com.example.nusamart.data.repository.order.OrderResult
import com.example.nusamart.data.repository.product.ProductRepository
import com.example.nusamart.data.repository.product.ProductResult
import com.example.nusamart.data.repository.shipping.ShippingRepository
import com.example.nusamart.data.repository.store.StoreRepository
import com.example.nusamart.data.repository.transaction.PaymentRepository  // ✅ ganti
import com.example.nusamart.data.repository.transaction.PaymentResult      // ✅ ganti
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
    private val paymentRepository: PaymentRepository,
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
            addresses.find { it.isDefault == 1 } ?: addresses.firstOrNull()
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
            val methods = paymentRepository.getActivePaymentMethods()
            val method = methods.find { it.idMethod == route.selectedPaymentMethodId }
            if (method != null) pName = method.methodName
        }

        val itemsInput = mutableListOf<OrderItemInput>()
        var subTotal = 0.0
        var mainStoreId = "STR-000001"

        val productsResult = productRepository.getAllProducts()
        val allProducts = if (productsResult is ProductResult.Success) {
            productsResult.data
        } else {
            emptyList()
        }

        if (route.fromCart) {
            if (userId != null) {
                val cartResponse = cartRepository.getCartWithItems()
                val checkedCartItems = cartResponse.items.filter { it.isChecked == 1 }

                val tempGroup = mutableMapOf<String, MutableList<OrderItemInput>>()

                for (cItem in checkedCartItems) {
                    for (product in allProducts) {
                        val detailResult = productRepository.getProductDetail(product.idProduct)

                        if (detailResult is ProductResult.Success) {
                            val matchedItem = detailResult.data.items.find { it.idItem == cItem.idItem }

                            if (matchedItem != null) {
                                val storeId = product.idStore
                                mainStoreId = storeId

                                val variations = matchedItem.variations ?: emptyList()
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

                                tempGroup.getOrPut(storeId) { mutableListOf() }.add(orderItem)
                                itemsInput.add(orderItem)
                                subTotal += (matchedItem.price * cItem.quantity)
                                break
                            }
                        }
                    }
                }
                groupedOrderItems = tempGroup
            }
        } else if (route.productId != null) {
            val product = allProducts.find { it.idProduct == route.productId }

            if (product != null) {
                val storeId = product.idStore
                mainStoreId = storeId

                val detailResult = productRepository.getProductDetail(route.productId)

                if (detailResult is ProductResult.Success) {
                    val itemData = detailResult.data.items.firstOrNull()
                    val price = itemData?.price ?: 50000.0

                    var displayName = product.productName
                    if (itemData != null) {
                        val variations = itemData.variations ?: emptyList()
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
        }

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

            val payRes = paymentRepository.createPayment(
                methodId = route.selectedPaymentMethodId!!,
                totalAmount = totalPaymentAmount,
                imageURL = null
            )

            if (payRes is PaymentResult.Success) {
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

                    if (orderRes is OrderResult.Success) {
                        val orderId = orderRes.orderId
                        createdOrderIds.add(orderId)
                        shippingRepository.createShipping(orderId, route.selectedCourierId!!)

                        val store = storeRepository.getStoreById(storeId)
                        val sellerId = store?.idSeller

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
                    val cartResponse = cartRepository.getCartWithItems()
                    cartResponse.items
                        .filter { it.isChecked == 1 }
                        .forEach { cartRepository.deleteItem(it.idCartItem) }
                }

                val combinedOrderIds = createdOrderIds.joinToString(", ")
                onSuccess(paymentId, combinedOrderIds)

            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
}