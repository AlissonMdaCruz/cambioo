package com.amdcruz.cambioo.ui.history

import com.amdcruz.cambioo.ui.home.VariationDirection

data class HistoryEntryUi(
    val observationDateText: String,
    val observationDateSpoken: String,
    val valueVisual: String,
    val valueSpoken: String,
    val variationVisual: String,
    val variationSpoken: String?,
    val variationDirection: VariationDirection,
)

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data object Empty : HistoryUiState
    data object Error : HistoryUiState
    data class Content(
        val currencyCode: String,
        val displayName: String,
        val entries: List<HistoryEntryUi>,
        val savedAtText: String,
        val sourceName: String,
        val notice: String?,
        val isRefreshing: Boolean,
    ) : HistoryUiState
}
