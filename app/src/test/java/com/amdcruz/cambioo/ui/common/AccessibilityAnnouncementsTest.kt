package com.amdcruz.cambioo.ui.common

import com.amdcruz.cambioo.domain.Currency
import com.amdcruz.cambioo.domain.QuoteFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class AccessibilityAnnouncementsTest {
    @Test
    fun quoteRow_dollarExample_announcesSpokenValueOnceWithoutVisualAmount() {
        val valueSpoken = QuoteFormatter.formatMoneySpoken(Currency.USD, BigDecimal("5.42"))
        val actual = AccessibilityAnnouncements.quoteRow(
            valueSpoken = valueSpoken,
            variationSpoken = null,
        )
        assertEquals("dólar, 5 reais e 42 centavos", actual)
        assertFalse(actual.contains("R$"))
        assertFalse(actual.contains("5,42"))
    }

    @Test
    fun quoteRow_withVariation_appendsSpokenComparisonOnly() {
        val valueSpoken = QuoteFormatter.formatMoneySpoken(Currency.USD, BigDecimal("5.42"))
        val variationSpoken = QuoteFormatter.formatVariationSpoken(BigDecimal("0.32"))
        val actual = AccessibilityAnnouncements.quoteRow(
            valueSpoken = valueSpoken,
            variationSpoken = variationSpoken,
        )
        assertEquals("dólar, 5 reais e 42 centavos, alta de 0,32 por cento", actual)
        assertFalse(actual.contains("+0,32%"))
    }

    @Test
    fun historyRow_includesSpokenDateAmountAndOptionalVariation() {
        val dateSpoken = QuoteFormatter.formatObservationDateSpoken(LocalDate.of(2026, 10, 30))
        val valueSpoken = QuoteFormatter.formatMoneySpoken(Currency.USD, BigDecimal("5.42"))
        val withVariation = AccessibilityAnnouncements.historyRow(
            observationDateSpoken = dateSpoken,
            valueSpoken = valueSpoken,
            variationSpoken = "queda de 0,32 por cento",
        )
        assertEquals(
            "30 de outubro de 2026, dólar, 5 reais e 42 centavos, queda de 0,32 por cento",
            withVariation,
        )
        val withoutVariation = AccessibilityAnnouncements.historyRow(
            observationDateSpoken = dateSpoken,
            valueSpoken = valueSpoken,
            variationSpoken = null,
        )
        assertEquals(
            "30 de outubro de 2026, dólar, 5 reais e 42 centavos",
            withoutVariation,
        )
    }
}
