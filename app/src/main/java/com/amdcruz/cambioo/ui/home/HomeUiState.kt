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
    ) : HomeUiState
}

object HomeCopy {
    const val ERROR_MESSAGE: String =
        "Não foi possível atualizar a cotação. Verifique a conexão e tente de novo."
    const val NOTICE_STALE: String =
        "Não foi possível atualizar. Mostrando a cotação guardada."
    const val EMPTY_MESSAGE: String = "Não há cotação neste período."
    const val LOADING_MESSAGE: String = "Carregando cotações"
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
