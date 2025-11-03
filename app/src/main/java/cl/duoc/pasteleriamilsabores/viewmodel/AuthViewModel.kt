package cl.duoc.pasteleriamilsabores.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import cl.duoc.pasteleriamilsabores.data.AuthUiState
import cl.duoc.pasteleriamilsabores.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Simulación de base de datos de usuarios en memoria
    private val users = mutableListOf<User>()

    fun register(name: String, email: String, birthDateStr: String, password: String, discountCode: String?) {
        if (!isEmailValid(email)) {
            _uiState.update { it.copy(errorMessage = "Error: El email no es válido.") }
            return
        }

        if (users.any { it.email.equals(email, ignoreCase = true) }) {
            _uiState.update { it.copy(errorMessage = "Error: El correo ya está registrado.") }
            return
        }

        val birthDate = try {
            LocalDate.parse(birthDateStr)
        } catch (e: DateTimeParseException) {
            _uiState.update { it.copy(errorMessage = "Error: Formato de fecha debe ser AAAA-MM-DD.") }
            return
        }

        val finalDiscountCode = if (discountCode.isNullOrBlank()) null else discountCode.uppercase()
        if (finalDiscountCode != null && finalDiscountCode != "FELICES50") {
            _uiState.update { it.copy(errorMessage = "Error: El código de descuento no es válido.") }
            return
        }

        val newUser = User(
            id = "user_${System.currentTimeMillis()}",
            name = name,
            email = email,
            birthDate = birthDate,
            discountCode = finalDiscountCode,
            passwordHash = password.hashCode().toString() // Simulación de hash
        )

        users.add(newUser)

        _uiState.update {
            it.copy(
                isAuthenticated = true,
                user = newUser,
                errorMessage = null
            )
        }
    }

    fun login(email: String, password: String) {
        val user = users.find { it.email.equals(email, ignoreCase = true) }

        if (user == null) {
            _uiState.update { it.copy(errorMessage = "Error: Usuario no encontrado.") }
            return
        }

        if (user.passwordHash != password.hashCode().toString()) { // Simulación de hash
            _uiState.update { it.copy(errorMessage = "Error: Contraseña incorrecta.") }
            return
        }

        _uiState.update {
            it.copy(
                isAuthenticated = true,
                user = user,
                errorMessage = null
            )
        }
    }

    fun updateProfile(currentPassword: String, newName: String, newEmail: String, newPassword: String?) {
        val currentUser = _uiState.value.user
        if (currentUser == null) {
            _uiState.update { it.copy(errorMessage = "Error: No hay sesión activa.") }
            return
        }

        if (currentUser.passwordHash != currentPassword.hashCode().toString()) {
            _uiState.update { it.copy(errorMessage = "Error: La contraseña actual es incorrecta.") }
            return
        }

        if (!isEmailValid(newEmail)) {
            _uiState.update { it.copy(errorMessage = "Error: El nuevo email no es válido.") }
            return
        }

        if (users.any { it.email.equals(newEmail, ignoreCase = true) && it.id != currentUser.id }) {
            _uiState.update { it.copy(errorMessage = "Error: El nuevo correo ya está en uso.") }
            return
        }

        val newPasswordHash = if (!newPassword.isNullOrBlank()) {
            newPassword.hashCode().toString()
        } else {
            currentUser.passwordHash
        }

        val updatedUser = currentUser.copy(
            name = newName,
            email = newEmail,
            passwordHash = newPasswordHash
        )

        val userIndex = users.indexOfFirst { it.id == currentUser.id }
        if (userIndex != -1) {
            users[userIndex] = updatedUser
        }

        _uiState.update { it.copy(user = updatedUser, errorMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun logout() {
        _uiState.update { AuthUiState() }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@") && email.length > 5
    }
}
