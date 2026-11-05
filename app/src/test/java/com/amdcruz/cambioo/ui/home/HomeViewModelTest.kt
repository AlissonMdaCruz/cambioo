package com.amdcruz.cambioo.ui.home

import app.cash.turbine.test
import com.amdcruz.cambioo.data.RatesSnapshot
import com.amdcruz.cambioo.data.RefreshResult
import com.amdcruz.cambioo.domain.Currency
import com.amdcruz.cambioo.domain.RateObservation
import com.amdcruz.cambioo.domain.RateSource
import com.amdcruz.cambioo.ui.common.RatesSessionStore
import com.amdcruz.cambioo.ui.common.RatesUiCopy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
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
    fun refresh_success_emitsContentWithQuotesAndNullNotice() = runTest {
        val snapshot = sampleSnapshot(
            observations = listOf(
                RateObservation(
                    date = LocalDate.of(2026, 11, 3),
                    currency = Currency.USD,
                    apiRate = BigDecimal("0.18408"),
                ),
                RateObservation(
                    date = LocalDate.of(2026, 11, 2),
                    currency = Currency.USD,
                    apiRate = BigDecimal("0.18500"),
                ),
                RateObservation(
                    date = LocalDate.of(2026, 11, 3),
                    currency = Currency.EUR,
                    apiRate = BigDecimal("0.16000"),
                ),
            ),
        )
        repository.refreshHandler = { RefreshResult.Success(snapshot) }
        val viewModel = HomeViewModel(repository, sessionStore)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Content)
            val content = state as HomeUiState.Content
            assertEquals(listOf("USD", "EUR"), content.quotes.map { it.currencyCode })
            assertNull(content.notice)
            assertFalse(content.isRefreshing)
            assertTrue(content.quotes.first().variationVisual.isNotBlank())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refresh_failureWithoutSnapshot_emitsError() = runTest {
        repository.refreshHandler = {
            RefreshResult.Failure(RuntimeException("offline"))
        }
        val viewModel = HomeViewModel(repository, sessionStore)
        advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals(HomeUiState.Error, expectMostRecentItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refresh_failureWithSnapshot_keepsContentAndNotice() = runTest {
        val snapshot = sampleSnapshot(
            observations = listOf(
                RateObservation(
                    date = LocalDate.of(2026, 11, 3),
                    currency = Currency.USD,
                    apiRate = BigDecimal("0.18408"),
                ),
            ),
        )
        repository.emitSnapshot(snapshot)
        repository.refreshHandler = {
            RefreshResult.Failure(RuntimeException("offline"))
        }
        val viewModel = HomeViewModel(repository, sessionStore)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Content)
            val content = state as HomeUiState.Content
            assertEquals(RatesUiCopy.NOTICE_STALE, content.notice)
            assertEquals("USD", content.quotes.single().currencyCode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refresh_emptyWithoutSnapshot_emitsEmpty() = runTest {
        repository.refreshHandler = { RefreshResult.Empty }
        val viewModel = HomeViewModel(repository, sessionStore)
        advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals(HomeUiState.Empty, expectMostRecentItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refresh_withExistingContent_keepsContentWithoutFullScreenLoading() = runTest {
        val snapshot = sampleSnapshot(
            observations = listOf(
                RateObservation(
                    date = LocalDate.of(2026, 11, 3),
                    currency = Currency.GBP,
                    apiRate = BigDecimal("0.14000"),
                ),
            ),
        )
        repository.emitSnapshot(snapshot)
        repository.refreshHandler = {
            RefreshResult.Success(snapshot)
        }
        val viewModel = HomeViewModel(repository, sessionStore)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is HomeUiState.Content)
        viewModel.refresh()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is HomeUiState.Content)
        assertNull((viewModel.uiState.value as HomeUiState.Content).notice)
        assertFalse((viewModel.uiState.value as HomeUiState.Content).isRefreshing)
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
