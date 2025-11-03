package cl.duoc.pasteleriamilsabores.data

// Un modelo de datos para representar un item en el carrito
data class CartItem(
    val product: Product,
    val specialMessage: String?
)