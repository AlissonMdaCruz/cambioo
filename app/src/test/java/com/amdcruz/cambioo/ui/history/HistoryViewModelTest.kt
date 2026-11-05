package com.amdcruz.cambioo.ui.history

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.amdcruz.cambioo.data.RatesSnapshot
import com.amdcruz.cambioo.data.RefreshResult
import com.amdcruz.cambioo.domain.Currency
import com.amdcruz.cambioo.domain.QuoteSeries
import com.amdcruz.cambioo.domain.RateObservation
import com.amdcruz.cambioo.domain.RateSource
import com.amdcruz.cambioo.ui.common.RatesSessionStore
import com.amdcruz.cambioo.ui.common.RatesUiCopy
import com.amdcruz.cambioo.ui.home.FakeRatesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeRatesRepository
    private lateinit var sessionStore: RatesSessionStore

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeRatesRepository()
        sessionStore = RatesSessionStore()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun history_ordersNewestFirst_andOldestHasNoVariation() = runTest {
        val day1 = LocalDate.of(2026, 10, 28)
        val day2 = LocalDate.of(2026, 10, 29)
        val day3 = LocalDate.of(2026, 10, 30)
        val snapshot = sampleSnapshot(
            observations = listOf(
                RateObservation(day1, Currency.USD, BigDecimal("0.18600")),
                RateObservation(day2, Currency.USD, BigDecimal("0.18500")),
                RateObservation(day3, Currency.USD, BigDecimal("0.18408")),
            ),
        )
        repository.emitSnapshot(snapshot)
        val viewModel = historyViewModel(Currency.USD)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertTrue(state is HistoryUiState.Content)
            val content = state as HistoryUiState.Content
            assertEquals(
                listOf("30/10/2026", "29/10/2026", "28/10/2026"),
                content.entries.map { it.observationDateText },
            )
            assertEquals("\u2014", content.entries.last().variationVisual)
            assertNull(content.entries.last().variationSpoken)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun history_latestVariationMatchesHomeSeries() = runTest {
        val observations = listOf(
            RateObservation(LocalDate.of(2026, 10, 29), Currency.USD, BigDecimal("0.18500")),
            RateObservation(LocalDate.of(2026, 10, 30), Currency.USD, BigDecimal("0.18408")),
        )
        val snapshot = sampleSnapshot(observations)
        repository.emitSnapshot(snapshot)
        val viewModel = historyViewModel(Currency.USD)
        advanceUntilIdle()

        val historyLatest = (viewModel.uiState.value as HistoryUiState.Content).entries.first()
        val homeLatest = QuoteSeries.latestByCurrency(observations).single()
        assertEquals(
            homeLatest.variationPercent?.setScale(2),
            // visual already formatted; compare spoken/percent via series
            homeLatest.variationPercent?.setScale(2),
        )
        assertTrue(historyLatest.variationVisual.startsWith("+") || historyLatest.variationVisual.startsWith("\u2212") || historyLatest.variationVisual == "0,00%")
        assertEquals(
            com.amdcruz.cambioo.domain.QuoteFormatter.formatVariationVisual(homeLatest.variationPercent),
            historyLatest.variationVisual,
        )
    }

    @Test
    fun history_skipsWeekendGapDays() = runTest {
        val friday = LocalDate.of(2026, 10, 30)
        val monday = LocalDate.of(2026, 11, 2)
        val snapshot = sampleSnapshot(
            observations = listOf(
                RateObservation(friday, Currency.USD, BigDecimal("0.18500")),
                RateObservation(monday, Currency.USD, BigDecimal("0.18408")),
            ),
        )
        repository.emitSnapshot(snapshot)
        val viewModel = historyViewModel(Currency.USD)
        advanceUntilIdle()

        val content = viewModel.uiState.value as HistoryUiState.Content
        assertEquals(2, content.entries.size)
        assertEquals(listOf("02/11/2026", "30/10/2026"), content.entries.map { it.observationDateText })
    }

    @Test
    fun refresh_failureWithSnapshot_keepsHistoryAndNotice() = runTest {
        val snapshot = sampleSnapshot(
            observations = listOf(
                RateObservation(LocalDate.of(2026, 10, 30), Currency.EUR, BigDecimal("0.16000")),
            ),
        )
        repository.emitSnapshot(snapshot)
        repository.refreshHandler = { RefreshResult.Failure(RuntimeException("offline")) }
        val viewModel = historyViewModel(Currency.EUR)
        advanceUntilIdle()
        viewModel.refresh()
        advanceUntilIdle()

        val content = viewModel.uiState.value as HistoryUiState.Content
        assertEquals(RatesUiCopy.NOTICE_STALE, content.notice)
        assertEquals("EUR", content.currencyCode)
    }

    @Test
    fun refresh_failureWithoutSnapshot_emitsError() = runTest {
        repository.refreshHandler = { RefreshResult.Failure(RuntimeException("offline")) }
        val viewModel = historyViewModel(Currency.USD)
        viewModel.refresh()
        advanceUntilIdle()
        assertEquals(HistoryUiState.Error, viewModel.uiState.value)
    }

    private fun historyViewModel(currency: Currency): HistoryViewModel {
        return HistoryViewModel(
            ratesRepository = repository,
            ratesSessionStore = sessionStore,
            savedStateHandle = SavedStateHandle(
                mapOf(HistoryViewModel.CURRENCY_CODE_KEY to currency.code),
            ),
        )
    }

    private fun sampleSnapshot(
        observations: List<RateObservation>,
    ): RatesSnapshot {
        return RatesSnapshot(
            savedAt = Instant.parse("2026-11-04T15:00:00Z"),
            from = LocalDate.of(2026, 10, 6),
            to = LocalDate.of(2026, 11, 4),
            sourceName = RateSource.DISPLAY_NAME,
            observations = observations,
        )
    }
}
