package com.amdcruz.cambioo.data

import com.amdcruz.cambioo.data.local.SnapshotLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSnapshotLocalDataSource(
    initial: RatesSnapshot? = null,
) : SnapshotLocalDataSource {
    private val snapshots = MutableStateFlow(initial)
    var replaceCallCount: Int = 0
        private set

    override fun observeSnapshot(): Flow<RatesSnapshot?> = snapshots.asStateFlow()

    override suspend fun replaceSnapshot(snapshot: RatesSnapshot) {
        replaceCallCount += 1
        snapshots.value = snapshot
    }

    fun setSnapshot(snapshot: RatesSnapshot?) {
        snapshots.value = snapshot
    }

    fun current(): RatesSnapshot? = snapshots.value
}
