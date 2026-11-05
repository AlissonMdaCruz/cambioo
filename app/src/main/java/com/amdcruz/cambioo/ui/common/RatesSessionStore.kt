package com.amdcruz.cambioo.ui.common

import com.amdcruz.cambioo.data.RatesRepository
import com.amdcruz.cambioo.data.RatesSnapshot
import com.amdcruz.cambioo.data.RefreshResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

enum class NonContentOutcome {
    Empty,
    Error,
}

data class RatesSessionState(
    val isRefreshing: Boolean = false,
    val notice: String? = null,
    val nonContentOutcome: NonContentOutcome? = null,
)

object RatesUiCopy {
    const val NOTICE_STALE: String =
        "Não foi possível atualizar. Mostrando a cotação guardada."
}

@Singleton
class RatesSessionStore @Inject constructor() {
    private val mutex = Mutex()
    private val mutableSession = MutableStateFlow(RatesSessionState())
    val session: StateFlow<RatesSessionState> = mutableSession.asStateFlow()

    suspend fun refresh(ratesRepository: RatesRepository): Unit {
        mutex.withLock {
            mutableSession.update { current -> current.copy(isRefreshing = true) }
            val hadContentBefore = ratesRepository.observeSnapshot()
                .first()
                .hasUsableObservations()
            when (ratesRepository.refreshWindow()) {
                is RefreshResult.Success -> {
                    mutableSession.update { current ->
                        current.copy(
                            isRefreshing = false,
                            notice = null,
                            nonContentOutcome = null,
                        )
                    }
                }
                RefreshResult.Empty -> {
                    mutableSession.update { current ->
                        current.copy(
                            isRefreshing = false,
                            notice = null,
                            nonContentOutcome = NonContentOutcome.Empty,
                        )
                    }
                }
                is RefreshResult.Failure -> {
                    val stillHasContent = ratesRepository.observeSnapshot()
                        .first()
                        .hasUsableObservations() || hadContentBefore
                    mutableSession.update { current ->
                        if (stillHasContent) {
                            current.copy(
                                isRefreshing = false,
                                notice = RatesUiCopy.NOTICE_STALE,
                                nonContentOutcome = null,
                            )
                        } else {
                            current.copy(
                                isRefreshing = false,
                                notice = null,
                                nonContentOutcome = NonContentOutcome.Error,
                            )
                        }
                    }
                }
            }
        }
    }
}

fun RatesSnapshot?.hasUsableObservations(): Boolean =
    this != null && observations.isNotEmpty()
