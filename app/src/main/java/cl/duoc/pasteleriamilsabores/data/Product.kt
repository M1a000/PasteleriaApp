package cl.duoc.pasteleriamilsabores.data

import androidx.annotation.DrawableRes

data class Product(
    val id: String,
    val category: String,
    val name: String,
    val price: Int,
    val description: String,
    @DrawableRes val image: Int? = null
)