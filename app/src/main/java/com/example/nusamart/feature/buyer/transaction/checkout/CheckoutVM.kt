package com.example.nusamart.feature.buyer.transaction.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nusamart.core.MyApplication
import com.example.nusamart.core.Routes
import com.example.nusamart.data.repository.order.OrderItemInput
import com.example.nusamart.data.repository.order.OrderRepository
import com.example.nusamart.data.repository.product.ProductRepository
import com.example.nusamart.data.repository.shipping.ShippingRepository
import com.example.nusamart.data.repository.transaction.TransactionRepository
import com.example.nusamart.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutVM(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val shippingRepository: ShippingRepository,
    private val transactionRepository: TransactionRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                CheckoutVM(app.userRepository, app.productRepository, app.shippingRepository, app.transactionRepository, app.orderRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()

    fun loadData(route: Routes.CheckoutRoute) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

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
        val itemsInput = mutableListOf<OrderItemInput>()
        var subTotal = 0.0
        var storeId = "STR-000001" // Default toko

        if (!route.fromCart && route.productId != null) {
            val product = productRepository.getAllProducts().find { it.idProduct == route.productId }
            if (product != null) {
                storeId = product.idStore
                val itemData = productRepository.getProductItems(route.productId).firstOrNull()
                val price = itemData?.price ?: 50000.0
                itemsInput.add(OrderItemInput(itemData?.idItem ?: "ITM-0", route.quantity, product.productName, price))
                subTotal = price * route.quantity
            }
        }

        _uiState.update {
            it.copy(
                isLoading = false, address = address, courierName = cName, paymentName = pName,
                items = itemsInput, subtotal = subTotal, orderStoreId = storeId
            )
        }
    }

    fun dismissDialog() = _uiState.update { it.copy(showAddressDialog = false) }

    fun placeOrder(route: Routes.CheckoutRoute, onSuccess: (String, String) -> Unit) = viewModelScope.launch {
        val state = _uiState.value
        if (state.address == null) {
            _uiState.update { it.copy(showAddressDialog = true) }
            return@launch
        }
        val userId = userRepository.getActiveUserId() ?: return@launch
        _uiState.update { it.copy(isLoading = true) }
        // Buat Order
        val orderRes = orderRepository.createOrder(
            userId = userId, storeId = state.orderStoreId, addressId = state.address.idAddress,
            items = state.items, shippingCost = state.shippingCost, servicePrice = state.serviceFee
        )

        if (orderRes is com.example.nusamart.data.repository.order.OrderResult.Success) {
            val orderId = orderRes.orderId
            // Buat Shipping & Payment
            shippingRepository.createShipping(orderId, route.selectedCourierId!!)
            val payRes = transactionRepository.createPayment(orderId, route.selectedPaymentMethodId!!)
            if (payRes is com.example.nusamart.data.repository.transaction.TransactionResult.Success) {
                onSuccess(payRes.transactionId, orderId)
            }
        }
    }
}