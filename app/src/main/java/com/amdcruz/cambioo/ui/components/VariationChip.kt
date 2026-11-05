package com.amdcruz.cambioo.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.amdcruz.cambioo.ui.home.VariationDirection
import com.amdcruz.cambioo.ui.theme.CambiooDimens

@Composable
fun VariationChip(
    text: String,
    direction: VariationDirection,
    modifier: Modifier = Modifier,
) {
    val color = variationColor(direction)
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.12f),
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(
                horizontal = CambiooDimens.ChipHorizontal,
                vertical = CambiooDimens.ChipVertical,
            ),
        )
    }
}
