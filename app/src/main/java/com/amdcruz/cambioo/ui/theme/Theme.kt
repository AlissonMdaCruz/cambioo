package com.amdcruz.cambioo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CambiooDarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    secondary = PositiveParity,
    onSecondary = OnPrimaryLight,
    tertiary = NegativeParity,
    background = CanvasBase,
    onBackground = TextHigh,
    surface = CanvasBase,
    onSurface = TextHigh,
    surfaceVariant = SurfaceContainer,
    onSurfaceVariant = TextMedium,
    outline = OutlineMuted,
    outlineVariant = OutlineHairline,
    error = ErrorSoft,
    onError = OnPrimaryLight,
)

@Composable
fun CambiooTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme || isSystemInDarkTheme()) {
        CambiooDarkColorScheme
    } else {
        CambiooDarkColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
