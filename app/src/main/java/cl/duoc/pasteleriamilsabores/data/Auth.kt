package cl.duoc.pasteleriamilsabores.data

import java.time.LocalDate

data class User(
    val id: String,
    val name: String,
    val email: String,
    val birthDate: LocalDate,
    val discountCode: String? = null,
    val passwordHash: String
)

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null
)
