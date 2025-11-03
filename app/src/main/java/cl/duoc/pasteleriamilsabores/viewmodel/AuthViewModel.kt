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

    fun loginOrRegister(email: String, birthDateStr: String, discountCode: String?) {
        if (!isEmailValid(email)) {
            _uiState.update { it.copy(errorMessage = "Error: El email no es válido.") }
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
            email = email,
            birthDate = birthDate,
            discountCode = finalDiscountCode
        )

        _uiState.update {
            it.copy(
                isAuthenticated = true,
                user = newUser,
                errorMessage = null
            )
        }
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
