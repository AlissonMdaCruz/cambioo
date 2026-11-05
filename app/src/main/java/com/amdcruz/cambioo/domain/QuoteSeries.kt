package com.amdcruz.cambioo.domain

object QuoteSeries {
    fun buildQuotedSeries(observations: List<RateObservation>): List<QuotedObservation> {
        val quotes = observations
            .mapNotNull(QuoteMath::toAppQuote)
            .sortedBy { it.date }
        return quotes.mapIndexed { index, quote ->
            val previous = quotes.getOrNull(index - 1)
            val variation = previous?.let { QuoteMath.variationPercent(quote.valueInReais, it.valueInReais) }
            QuotedObservation(quote = quote, variationPercent = variation)
        }
    }

    fun latestByCurrency(observations: List<RateObservation>): List<QuotedObservation> {
        return Currency.displayOrder.mapNotNull { currency ->
            val forCurrency = observations.filter { it.currency == currency }
            if (forCurrency.isEmpty()) {
                null
            } else {
                buildQuotedSeries(forCurrency).lastOrNull()
            }
        }
    }

    fun historyForCurrency(
        observations: List<RateObservation>,
        currency: Currency,
    ): List<QuotedObservation> {
        val series = buildQuotedSeries(observations.filter { it.currency == currency })
        return series.asReversed()
    }
}
