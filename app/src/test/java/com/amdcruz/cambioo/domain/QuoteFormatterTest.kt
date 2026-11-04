package com.amdcruz.cambioo.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal

class QuoteFormatterTest {
    @Test
    fun formatMoneyVisual_usesBrazilianDecimal() {
        assertEquals("R$ 5,43", QuoteFormatter.formatMoneyVisual(BigDecimal("5.43")))
    }

    @Test
    fun formatVariationVisual_positiveNegativeStableAndAbsent() {
        assertEquals("+0,32%", QuoteFormatter.formatVariationVisual(BigDecimal("0.32")))
        assertEquals("\u22120,32%", QuoteFormatter.formatVariationVisual(BigDecimal("-0.32")))
        assertEquals("0,00%", QuoteFormatter.formatVariationVisual(BigDecimal("0.00")))
        assertEquals("\u2014", QuoteFormatter.formatVariationVisual(null))
    }

    @Test
    fun formatAmountInWords_singularPluralTable() {
        assertEquals("0 reais", QuoteFormatter.formatAmountInWords(BigDecimal("0.00")))
        assertEquals("1 centavo", QuoteFormatter.formatAmountInWords(BigDecimal("0.01")))
        assertEquals("2 centavos", QuoteFormatter.formatAmountInWords(BigDecimal("0.02")))
        assertEquals("1 real", QuoteFormatter.formatAmountInWords(BigDecimal("1.00")))
        assertEquals("1 real e 1 centavo", QuoteFormatter.formatAmountInWords(BigDecimal("1.01")))
        assertEquals("2 reais", QuoteFormatter.formatAmountInWords(BigDecimal("2.00")))
        assertEquals("5 reais e 42 centavos", QuoteFormatter.formatAmountInWords(BigDecimal("5.42")))
    }

    @Test
    fun formatMoneySpoken_dollarExample() {
        val actual = QuoteFormatter.formatMoneySpoken(Currency.USD, BigDecimal("5.42"))
        assertEquals("dólar, 5 reais e 42 centavos", actual)
    }

    @Test
    fun formatVariationSpoken_riseFallStableAbsent() {
        assertEquals(
            "alta de 0,32 por cento",
            QuoteFormatter.formatVariationSpoken(BigDecimal("0.32")),
        )
        assertEquals(
            "queda de 0,32 por cento",
            QuoteFormatter.formatVariationSpoken(BigDecimal("-0.32")),
        )
        assertEquals("estável", QuoteFormatter.formatVariationSpoken(BigDecimal("0.00")))
        assertNull(QuoteFormatter.formatVariationSpoken(null))
    }
}
