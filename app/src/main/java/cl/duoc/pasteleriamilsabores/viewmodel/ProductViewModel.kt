package cl.duoc.pasteleriamilsabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.pasteleriamilsabores.data.Product
import cl.duoc.pasteleriamilsabores.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    init {
        viewModelScope.launch {
            _products.value = ProductRepository.getAllProducts()
        }
    }

    fun addProduct(product: Product) {
        _products.update { currentList ->
            listOf(product) + currentList
        }
    }
    
    fun updateProduct(product: Product) {
        _products.update { currentList ->
            currentList.map {
                if (it.id == product.id) product else it
            }
        }
    }

    fun deleteProduct(productId: String) {
        _products.update { currentList ->
            currentList.filterNot { it.id == productId }
        }
    }
}