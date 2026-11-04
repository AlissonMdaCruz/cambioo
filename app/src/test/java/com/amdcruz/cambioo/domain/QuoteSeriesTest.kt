package com.amdcruz.cambioo.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class QuoteSeriesTest {
    @Test
    fun buildQuotedSeries_skipsWeekendGap_usesPreviousPublished() {
        val friday = LocalDate.of(2026, 10, 30)
        val monday = LocalDate.of(2026, 11, 2)
        val observations = listOf(
            RateObservation(friday, Currency.USD, BigDecimal("0.18500")),
            RateObservation(monday, Currency.USD, BigDecimal("0.18408")),
        )
        val series = QuoteSeries.buildQuotedSeries(observations)
        assertEquals(2, series.size)
        assertNull(series[0].variationPercent)
        assertEquals(BigDecimal("5.43"), series[1].quote.valueInReais)
        assertEquals(BigDecimal("5.41"), series[0].quote.valueInReais)
        val expectedVariation = QuoteMath.variationPercent(
            current = series[1].quote.valueInReais,
            previous = series[0].quote.valueInReais,
        )
        assertEquals(expectedVariation, series[1].variationPercent)
    }

    @Test
    fun historyForCurrency_newestFirst_oldestWithoutVariation() {
        val day1 = LocalDate.of(2026, 10, 28)
        val day2 = LocalDate.of(2026, 10, 29)
        val day3 = LocalDate.of(2026, 10, 30)
        val observations = listOf(
            RateObservation(day1, Currency.USD, BigDecimal("0.18600")),
            RateObservation(day2, Currency.USD, BigDecimal("0.18500")),
            RateObservation(day3, Currency.USD, BigDecimal("0.18408")),
        )
        val history = QuoteSeries.historyForCurrency(observations, Currency.USD)
        assertEquals(listOf(day3, day2, day1), history.map { it.quote.date })
        assertNull(history.last().variationPercent)
        assertTrue(history.first().variationPercent != null)
    }

    @Test
    fun latestByCurrency_keepsDisplayOrder_andDropsInvalid() {
        val date = LocalDate.of(2026, 10, 30)
        val observations = listOf(
            RateObservation(date, Currency.EUR, BigDecimal("0.16000")),
            RateObservation(date, Currency.USD, BigDecimal("0.18408")),
            RateObservation(date, Currency.CHF, BigDecimal.ZERO),
            RateObservation(date, Currency.GBP, BigDecimal("0.14000")),
        )
        val latest = QuoteSeries.latestByCurrency(observations)
        assertEquals(
            listOf(Currency.USD, Currency.EUR, Currency.GBP),
            latest.map { it.quote.currency },
        )
    }
}
