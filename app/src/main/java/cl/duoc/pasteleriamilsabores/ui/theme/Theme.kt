package cl.duoc.pasteleriamilsabores.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Chocolate,
    secondary = RosaSuave,
    background = MarronOscuro,
    surface = MarronOscuro,
    onPrimary = CremaPastel,
    onSecondary = CremaPastel,
    onBackground = CremaPastel,
    onSurface = CremaPastel,
    onSurfaceVariant = Blanco
)

private val LightColorScheme = lightColorScheme(
    primary = Chocolate,
    secondary = RosaSuave,
    background = CremaPastel,
    surface = CremaPastel,
    onPrimary = CremaPastel,
    onSecondary = MarronOscuro,
    onBackground = MarronOscuro,
    onSurface = MarronOscuro,
    onSurfaceVariant = Blanco
)

@Composable
fun PasteleriaMilSaboresTheme(
    darkTheme: Boolean = false, // Forzar tema claro
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true // Forzar status bar para tema claro
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
