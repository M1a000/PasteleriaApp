// En: app/src.../ui/theme/Type.kt
package cl.duoc.pasteleriamilsabores.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cl.duoc.pasteleriamilsabores.R // Asegúrate que esta R sea la de tu proyecto

// Cargar las fuentes desde res/font
val lato = FontFamily(
    Font(R.font.lato_regular, FontWeight.Normal)
)
val pacifico = FontFamily(
    Font(R.font.pacifico_regular, FontWeight.Normal)
)

// Definir los estilos de texto
val AppTypography = Typography(
    // Fuente de Encabezado: Pacifico [cite: 79]
    headlineLarge = TextStyle(
        fontFamily = pacifico,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        color = MarronOscuro // Texto Principal [cite: 83]
    ),
    // Fuente Principal: Lato [cite: 76]
    bodyLarge = TextStyle(
        fontFamily = lato,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = MarronOscuro
    ),
    // Texto Secundario: Gris Claro [cite: 86]
    labelSmall = TextStyle(
        fontFamily = lato,
        fontSize = 12.sp,
        color = Blanco
    )
)