package com.amdcruz.cambioo.ui.theme

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
    surfaceContainerLowest = CanvasBase,
    surfaceContainerLow = SurfaceElevation1,
    surfaceContainer = SurfaceElevation1,
    surfaceContainerHigh = SurfacePressed,
    surfaceContainerHighest = SurfaceHigh,
    outline = OutlineMuted,
    outlineVariant = OutlineHairline,
    error = ErrorSoft,
    onError = OnPrimaryLight,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerLight,
)

@Composable
fun CambiooTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CambiooDarkColorScheme,
        typography = Typography,
        shapes = CambiooShapes,
        content = content,
    )
}
