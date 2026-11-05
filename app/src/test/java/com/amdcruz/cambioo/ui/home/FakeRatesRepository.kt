package com.amdcruz.cambioo.ui.home

import com.amdcruz.cambioo.data.RatesRepository
import com.amdcruz.cambioo.data.RatesSnapshot
import com.amdcruz.cambioo.data.RefreshResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeRatesRepository(
    initialSnapshot: RatesSnapshot? = null,
) : RatesRepository {
    private val snapshots = MutableStateFlow(initialSnapshot)
    var refreshHandler: suspend () -> RefreshResult = {
        RefreshResult.Failure(IllegalStateException("refresh handler not set"))
    }
    var refreshCallCount: Int = 0
        private set

    override fun observeSnapshot(): Flow<RatesSnapshot?> = snapshots.asStateFlow()

    override suspend fun refreshWindow(): RefreshResult {
        refreshCallCount += 1
        val result = refreshHandler()
        if (result is RefreshResult.Success) {
            snapshots.value = result.snapshot
        }
        return result
    }

    fun emitSnapshot(snapshot: RatesSnapshot?) {
        snapshots.value = snapshot
    }
}
