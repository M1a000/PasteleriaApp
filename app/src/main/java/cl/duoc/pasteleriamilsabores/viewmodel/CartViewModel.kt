package cl.duoc.pasteleriamilsabores.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.pasteleriamilsabores.data.AuthUiState
import cl.duoc.pasteleriamilsabores.data.CartItem
import cl.duoc.pasteleriamilsabores.data.Product
import cl.duoc.pasteleriamilsabores.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Int = 0,
    val discount: Int = 0,
    val total: Int = 0,
    val discountMessage: String? = null
)

@RequiresApi(Build.VERSION_CODES.O)
class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    private val _authState = MutableStateFlow(AuthUiState())

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(_cartItems, _authState) { items, authState ->
                calculateTotals(items, authState.user)
            }.collect { cartUiState ->
                _uiState.value = cartUiState
            }
        }
    }

    fun addToCart(product: Product, specialMessage: String?) {
        _cartItems.update { currentItems ->
            val newItem = CartItem(
                product = product,
                specialMessage = specialMessage
            )
            currentItems + newItem
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun updateAuthState(authState: AuthUiState) {
        _authState.value = authState
    }

    private fun calculateTotals(items: List<CartItem>, user: User?): CartUiState {
        val subtotal = items.sumOf { it.product.price }
        var discount = 0
        var discountMessage: String? = null

        if (user != null) {
            val today = LocalDate.now()
            val age = Period.between(user.birthDate, today).years

            val isDuocEmail = user.email.endsWith("@duoc.cl", ignoreCase = true) ||
                    user.email.endsWith("@duocuc.cl", ignoreCase = true)

            if (isDuocEmail &&
                user.birthDate.month == today.month &&
                user.birthDate.dayOfMonth == today.dayOfMonth
            ) {
                val cakeTotal = items
                    .filter { it.product.category.startsWith("Torta", ignoreCase = true) }
                    .sumOf { it.product.price }

                discount = cakeTotal
                discountMessage = "Descuento Cumpleaños Duoc (100% en Tortas)"
            } else if (age > 50) {
                discount = (subtotal * 0.15).toInt()
                discountMessage = "Descuento +50 Años (15%)"
            } else if (user.discountCode == "FELICES50") {
                discount = (subtotal * 0.10).toInt()
                discountMessage = "Descuento Código 'FELICES50' (10%)"
            }
        }

        val total = subtotal - discount

        return CartUiState(
            items = items,
            subtotal = subtotal,
            discount = discount,
            total = total,
            discountMessage = discountMessage
        )
    }
}
