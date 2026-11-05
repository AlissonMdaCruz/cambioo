package com.amdcruz.cambioo.ui.home

import java.math.BigDecimal

data class QuoteUi(
    val currencyCode: String,
    val displayName: String,
    val valueVisual: String,
    val valueSpoken: String,
    val variationVisual: String,
    val variationSpoken: String?,
    val observationDateText: String,
    val variationDirection: VariationDirection,
)

enum class VariationDirection {
    Up,
    Down,
    Flat,
    None,
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data object Error : HomeUiState
    data class Content(
        val quotes: List<QuoteUi>,
        val latestQuoteDateText: String,
        val savedAtText: String,
        val sourceName: String,
        val notice: String?,
        val isRefreshing: Boolean,
    ) : HomeUiState
}

fun variationDirectionOf(variationPercent: BigDecimal?): VariationDirection {
    if (variationPercent == null) {
        return VariationDirection.None
    }
    return when (variationPercent.signum()) {
        1 -> VariationDirection.Up
        -1 -> VariationDirection.Down
        else -> VariationDirection.Flat
    }
}
