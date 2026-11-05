package com.amdcruz.cambioo.ui.home

import com.amdcruz.cambioo.data.RatesSnapshot
import com.amdcruz.cambioo.domain.QuoteFormatter
import com.amdcruz.cambioo.domain.QuoteSeries
import com.amdcruz.cambioo.domain.QuotedObservation
import com.amdcruz.cambioo.ui.common.RatesSessionState

object HomeQuoteMapper {
    fun toContent(
        snapshot: RatesSnapshot,
        sessionState: RatesSessionState,
    ): HomeUiState.Content {
        val quoted = QuoteSeries.latestByCurrency(snapshot.observations)
        val quotes = quoted.map { item -> item.toQuoteUi() }
        val latestDate = quoted.maxOfOrNull { item -> item.quote.date } ?: snapshot.to
        return HomeUiState.Content(
            quotes = quotes,
            latestQuoteDateText = QuoteFormatter.formatObservationDate(latestDate),
            savedAtText = QuoteFormatter.formatSavedAt(snapshot.savedAt),
            sourceName = snapshot.sourceName,
            notice = sessionState.notice,
            isRefreshing = sessionState.isRefreshing,
        )
    }

    private fun QuotedObservation.toQuoteUi(): QuoteUi {
        val currency = quote.currency
        return QuoteUi(
            currencyCode = currency.code,
            displayName = currency.displayName,
            valueVisual = QuoteFormatter.formatMoneyVisual(quote.valueInReais),
            valueSpoken = QuoteFormatter.formatMoneySpoken(currency, quote.valueInReais),
            variationVisual = QuoteFormatter.formatVariationVisual(variationPercent),
            variationSpoken = QuoteFormatter.formatVariationSpoken(variationPercent),
            observationDateText = QuoteFormatter.formatObservationDate(quote.date),
            variationDirection = variationDirectionOf(variationPercent),
        )
    }
}
