package com.amdcruz.cambioo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amdcruz.cambioo.data.RatesRepository
import com.amdcruz.cambioo.data.RatesSnapshot
import com.amdcruz.cambioo.data.RefreshResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ratesRepository: RatesRepository,
) : ViewModel() {
    private val session = MutableStateFlow(SessionState())

    val uiState: StateFlow<HomeUiState> = combine(
        ratesRepository.observeSnapshot(),
        session,
    ) { snapshot, sessionState ->
        mapUiState(snapshot = snapshot, sessionState = sessionState)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HomeUiState.Loading,
    )

    init {
        refresh()
    }

    fun refresh(): Unit {
        viewModelScope.launch {
            session.update { current -> current.copy(isRefreshing = true) }
            val hadContentBefore = ratesRepository.observeSnapshot()
                .first()
                .hasUsableObservations()
            when (ratesRepository.refreshWindow()) {
                is RefreshResult.Success -> {
                    session.update { current ->
                        current.copy(
                            isRefreshing = false,
                            notice = null,
                            nonContentOutcome = null,
                        )
                    }
                }
                RefreshResult.Empty -> {
                    session.update { current ->
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
                    session.update { current ->
                        if (stillHasContent) {
                            current.copy(
                                isRefreshing = false,
                                notice = HomeCopy.NOTICE_STALE,
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

    private fun mapUiState(
        snapshot: RatesSnapshot?,
        sessionState: SessionState,
    ): HomeUiState {
        val usable = snapshot?.takeIf { it.hasUsableObservations() }
        if (usable != null) {
            return HomeQuoteMapper.toContent(snapshot = usable, notice = sessionState.notice)
        }
        if (sessionState.isRefreshing) {
            return HomeUiState.Loading
        }
        return when (sessionState.nonContentOutcome) {
            NonContentOutcome.Empty -> HomeUiState.Empty
            NonContentOutcome.Error -> HomeUiState.Error
            null -> HomeUiState.Loading
        }
    }

    private data class SessionState(
        val isRefreshing: Boolean = false,
        val notice: String? = null,
        val nonContentOutcome: NonContentOutcome? = null,
    )

    private enum class NonContentOutcome {
        Empty,
        Error,
    }
}

private fun RatesSnapshot?.hasUsableObservations(): Boolean =
    this != null && observations.isNotEmpty()
