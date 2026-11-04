package com.amdcruz.cambioo.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class QuoteMathTest {
    @Test
    fun quoteFromApiRate_validUsdRate_returnsScale2HalfUp() {
        val inputRate = BigDecimal("0.18408")
        val expectedQuote = BigDecimal("5.43")
        val actualQuote = QuoteMath.quoteFromApiRate(inputRate)
        assertEquals(expectedQuote, actualQuote)
    }

    @Test
    fun quoteFromApiRate_zeroRate_returnsNull() {
        assertNull(QuoteMath.quoteFromApiRate(BigDecimal.ZERO))
    }

    @Test
    fun quoteFromApiRate_negativeRate_returnsNull() {
        assertNull(QuoteMath.quoteFromApiRate(BigDecimal("-0.1")))
    }

    @Test
    fun variationPercent_rise_returnsPositive() {
        val current = BigDecimal("5.43")
        val previous = BigDecimal("5.41")
        val actual = QuoteMath.variationPercent(current, previous)
        assertEquals(BigDecimal("0.37"), actual)
    }

    @Test
    fun variationPercent_fall_returnsNegative() {
        val current = BigDecimal("5.41")
        val previous = BigDecimal("5.43")
        val actual = QuoteMath.variationPercent(current, previous)
        assertEquals(BigDecimal("-0.37"), actual)
    }

    @Test
    fun variationPercent_stable_returnsZero() {
        val value = BigDecimal("5.42")
        val actual = QuoteMath.variationPercent(value, value)
        assertEquals(BigDecimal("0.00"), actual)
    }

    @Test
    fun windowDates_includesThirtyCalendarDays() {
        val today = LocalDate.of(2026, 11, 4)
        val actual = WindowDates.forLocalDate(today)
        assertEquals(LocalDate.of(2026, 10, 6), actual.from)
        assertEquals(today, actual.to)
    }
}
