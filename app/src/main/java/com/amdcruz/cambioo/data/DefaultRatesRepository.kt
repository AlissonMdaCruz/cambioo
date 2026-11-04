package com.amdcruz.cambioo.data

import com.amdcruz.cambioo.data.local.SnapshotLocalDataSource
import com.amdcruz.cambioo.data.remote.RatesRemoteDataSource
import com.amdcruz.cambioo.domain.QuoteMath
import com.amdcruz.cambioo.domain.RateSource
import com.amdcruz.cambioo.domain.WindowDates
import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class DefaultRatesRepository @Inject constructor(
    private val remoteDataSource: RatesRemoteDataSource,
    private val localDataSource: SnapshotLocalDataSource,
    private val clock: Clock,
) : RatesRepository {
    override fun observeSnapshot(): Flow<RatesSnapshot?> = localDataSource.observeSnapshot()

    override suspend fun refreshWindow(): RefreshResult {
        return try {
            val today = LocalDate.now(clock.withZone(QuoteMath.SAO_PAULO))
            val window = WindowDates.forLocalDate(today)
            val observations = remoteDataSource.fetchWindow(window)
            val usable = observations.filter { QuoteMath.quoteFromApiRate(it.apiRate) != null }
            if (usable.isEmpty()) {
                RefreshResult.Empty
            } else {
                val snapshot = RatesSnapshot(
                    savedAt = Instant.now(clock),
                    from = window.from,
                    to = window.to,
                    sourceName = RateSource.DISPLAY_NAME,
                    observations = usable,
                )
                localDataSource.replaceSnapshot(snapshot)
                RefreshResult.Success(snapshot)
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            RefreshResult.Failure(error)
        }
    }
}
