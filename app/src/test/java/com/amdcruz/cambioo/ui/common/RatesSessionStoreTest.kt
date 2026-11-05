package com.amdcruz.cambioo.ui.common

import com.amdcruz.cambioo.data.RatesSnapshot
import com.amdcruz.cambioo.data.RefreshResult
import com.amdcruz.cambioo.domain.Currency
import com.amdcruz.cambioo.domain.RateObservation
import com.amdcruz.cambioo.domain.RateSource
import com.amdcruz.cambioo.ui.home.FakeRatesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

class RatesSessionStoreTest {
    @Test
    fun concurrentRefresh_serializesCallsAndKeepsLatestSuccess() = runTest {
        val repository = FakeRatesRepository()
        val store = RatesSessionStore()
        var callIndex = 0
        repository.refreshHandler = {
            callIndex += 1
            val index = callIndex
            RefreshResult.Success(
                RatesSnapshot(
                    savedAt = Instant.parse("2026-11-04T15:00:0${index}Z"),
                    from = LocalDate.of(2026, 10, 6),
                    to = LocalDate.of(2026, 11, 4),
                    sourceName = RateSource.DISPLAY_NAME,
                    observations = listOf(
                        RateObservation(
                            date = LocalDate.of(2026, 11, 3),
                            currency = Currency.USD,
                            apiRate = BigDecimal("0.18${index}00"),
                        ),
                    ),
                ),
            )
        }
        val first = async { store.refresh(repository) }
        val second = async { store.refresh(repository) }
        first.await()
        second.await()
        assertEquals(2, repository.refreshCallCount)
        assertFalse(store.session.value.isRefreshing)
        assertNull(store.session.value.notice)
        val snapshot = repository.observeSnapshot().first()
        assertEquals(BigDecimal("0.18200"), snapshot?.observations?.single()?.apiRate)
    }
}
