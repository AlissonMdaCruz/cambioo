package com.amdcruz.cambioo.ui.history

import com.amdcruz.cambioo.data.RatesSnapshot
import com.amdcruz.cambioo.domain.Currency
import com.amdcruz.cambioo.domain.QuoteFormatter
import com.amdcruz.cambioo.domain.QuoteSeries
import com.amdcruz.cambioo.domain.QuotedObservation
import com.amdcruz.cambioo.ui.common.RatesSessionState
import com.amdcruz.cambioo.ui.home.variationDirectionOf

object HistoryMapper {
    fun toContent(
        snapshot: RatesSnapshot,
        currency: Currency,
        sessionState: RatesSessionState,
    ): HistoryUiState.Content? {
        val entries = QuoteSeries.historyForCurrency(snapshot.observations, currency)
        if (entries.isEmpty()) {
            return null
        }
        return HistoryUiState.Content(
            currencyCode = currency.code,
            displayName = currency.displayName,
            entries = entries.map { quoted -> quoted.toEntryUi(currency) },
            savedAtText = QuoteFormatter.formatSavedAt(snapshot.savedAt),
            sourceName = snapshot.sourceName,
            notice = sessionState.notice,
            isRefreshing = sessionState.isRefreshing,
        )
    }

    private fun QuotedObservation.toEntryUi(currency: Currency): HistoryEntryUi {
        return HistoryEntryUi(
            observationDateText = QuoteFormatter.formatObservationDate(quote.date),
            observationDateSpoken = QuoteFormatter.formatObservationDateSpoken(quote.date),
            valueVisual = QuoteFormatter.formatMoneyVisual(quote.valueInReais),
            valueSpoken = QuoteFormatter.formatMoneySpoken(currency, quote.valueInReais),
            variationVisual = QuoteFormatter.formatVariationVisual(variationPercent),
            variationSpoken = QuoteFormatter.formatVariationSpoken(variationPercent),
            variationDirection = variationDirectionOf(variationPercent),
        )
    }
}
