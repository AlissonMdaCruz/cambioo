package com.amdcruz.cambioo.data

import app.cash.turbine.test
import com.amdcruz.cambioo.domain.Currency
import com.amdcruz.cambioo.domain.RateObservation
import com.amdcruz.cambioo.domain.RateSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class DefaultRatesRepositoryTest {
    private val fixedInstant: Instant = Instant.parse("2026-11-04T15:00:00Z")
    private val clock: Clock = Clock.fixed(fixedInstant, ZoneOffset.UTC)
    private val remote = FakeRatesRemoteDataSource()
    private val local = FakeSnapshotLocalDataSource()
    private val repository = DefaultRatesRepository(
        remoteDataSource = remote,
        localDataSource = local,
        clock = clock,
    )

    @Test
    fun refreshWindow_success_replacesSnapshotCompletely() = runTest {
        val existing = RatesSnapshot(
            savedAt = Instant.parse("2026-10-01T12:00:00Z"),
            from = LocalDate.of(2026, 9, 1),
            to = LocalDate.of(2026, 9, 30),
            sourceName = RateSource.DISPLAY_NAME,
            observations = listOf(
                RateObservation(
                    date = LocalDate.of(2026, 9, 30),
                    currency = Currency.USD,
                    apiRate = BigDecimal("0.20000"),
                ),
            ),
        )
        local.setSnapshot(existing)
        remote.observationsToReturn = listOf(
            RateObservation(
                date = LocalDate.of(2026, 11, 3),
                currency = Currency.USD,
                apiRate = BigDecimal("0.18408"),
            ),
            RateObservation(
                date = LocalDate.of(2026, 11, 3),
                currency = Currency.EUR,
                apiRate = BigDecimal("0.16000"),
            ),
        )

        val result = repository.refreshWindow()

        assertTrue(result is RefreshResult.Success)
        val current = local.current()!!
        assertEquals(2, current.observations.size)
        assertEquals(fixedInstant, current.savedAt)
        assertEquals(LocalDate.of(2026, 10, 6), current.from)
        assertEquals(LocalDate.of(2026, 11, 4), current.to)
        assertEquals(RateSource.DISPLAY_NAME, current.sourceName)
        assertEquals(1, local.replaceCallCount)
    }

    @Test
    fun refreshWindow_failure_preservesExistingSnapshot() = runTest {
        val existing = RatesSnapshot(
            savedAt = Instant.parse("2026-10-01T12:00:00Z"),
            from = LocalDate.of(2026, 9, 1),
            to = LocalDate.of(2026, 9, 30),
            sourceName = RateSource.DISPLAY_NAME,
            observations = listOf(
                RateObservation(
                    date = LocalDate.of(2026, 9, 30),
                    currency = Currency.USD,
                    apiRate = BigDecimal("0.18408"),
                ),
            ),
        )
        local.setSnapshot(existing)
        remote.errorToThrow = RuntimeException("network down")

        val result = repository.refreshWindow()

        assertTrue(result is RefreshResult.Failure)
        assertEquals(existing, local.current())
        assertEquals(0, local.replaceCallCount)
    }

    @Test
    fun refreshWindow_allInvalidRates_returnsEmptyWithoutWriting() = runTest {
        remote.observationsToReturn = listOf(
            RateObservation(
                date = LocalDate.of(2026, 11, 3),
                currency = Currency.USD,
                apiRate = BigDecimal.ZERO,
            ),
        )

        val result = repository.refreshWindow()

        assertTrue(result is RefreshResult.Empty)
        assertNull(local.current())
        assertEquals(0, local.replaceCallCount)
    }

    @Test
    fun observeSnapshot_emitsLocalValue() = runTest {
        val snapshot = RatesSnapshot(
            savedAt = fixedInstant,
            from = LocalDate.of(2026, 10, 6),
            to = LocalDate.of(2026, 11, 4),
            sourceName = RateSource.DISPLAY_NAME,
            observations = emptyList(),
        )
        local.setSnapshot(snapshot)

        repository.observeSnapshot().test {
            assertEquals(snapshot, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
