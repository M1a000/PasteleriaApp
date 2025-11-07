package cl.duoc.pasteleriamilsabores.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WishlistUiState(
    val wishlistedProductIds: Set<String> = emptySet()
)

class WishlistViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WishlistUiState())
    val uiState: StateFlow<WishlistUiState> = _uiState.asStateFlow()

    fun isWishlisted(productId: String): Boolean {
        return _uiState.value.wishlistedProductIds.contains(productId)
    }

    fun toggleWishlist(productId: String) {
        _uiState.update { currentState ->
            val updatedIds = currentState.wishlistedProductIds.toMutableSet()
            if (updatedIds.contains(productId)) {
                updatedIds.remove(productId)
            } else {
                updatedIds.add(productId)
            }
            currentState.copy(wishlistedProductIds = updatedIds)
        }
    }
}
