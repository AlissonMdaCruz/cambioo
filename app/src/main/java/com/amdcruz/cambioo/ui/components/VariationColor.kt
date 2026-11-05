package com.amdcruz.cambioo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.amdcruz.cambioo.ui.home.VariationDirection
import com.amdcruz.cambioo.ui.theme.NegativeParity
import com.amdcruz.cambioo.ui.theme.NeutralParity
import com.amdcruz.cambioo.ui.theme.PositiveParity

@Composable
fun variationColor(direction: VariationDirection): Color =
    when (direction) {
        VariationDirection.Up -> PositiveParity
        VariationDirection.Down -> NegativeParity
        VariationDirection.Flat, VariationDirection.None -> NeutralParity
    }
