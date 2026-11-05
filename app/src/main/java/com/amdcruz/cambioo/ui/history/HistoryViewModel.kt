package com.amdcruz.cambioo.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amdcruz.cambioo.data.RatesRepository
import com.amdcruz.cambioo.data.RatesSnapshot
import com.amdcruz.cambioo.domain.Currency
import com.amdcruz.cambioo.ui.common.NonContentOutcome
import com.amdcruz.cambioo.ui.common.RatesSessionState
import com.amdcruz.cambioo.ui.common.RatesSessionStore
import com.amdcruz.cambioo.ui.common.hasUsableObservations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val ratesRepository: RatesRepository,
    private val ratesSessionStore: RatesSessionStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val currency: Currency = Currency.fromCode(
        checkNotNull(savedStateHandle.get<String>(CURRENCY_CODE_KEY)) {
            "Missing navigation argument $CURRENCY_CODE_KEY"
        },
    ) ?: error("Unsupported currency code")

    val uiState: StateFlow<HistoryUiState> = combine(
        ratesRepository.observeSnapshot(),
        ratesSessionStore.session,
    ) { snapshot, sessionState ->
        mapUiState(snapshot = snapshot, sessionState = sessionState)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HistoryUiState.Loading,
    )

    fun refresh(): Unit {
        viewModelScope.launch {
            ratesSessionStore.refresh(ratesRepository)
        }
    }

    private fun mapUiState(
        snapshot: RatesSnapshot?,
        sessionState: RatesSessionState,
    ): HistoryUiState {
        val usableSnapshot = snapshot?.takeIf { it.hasUsableObservations() }
        val content = usableSnapshot?.let { current ->
            HistoryMapper.toContent(
                snapshot = current,
                currency = currency,
                sessionState = sessionState,
            )
        }
        if (content != null) {
            return content
        }
        if (sessionState.isRefreshing) {
            return HistoryUiState.Loading
        }
        return when (sessionState.nonContentOutcome) {
            NonContentOutcome.Empty -> HistoryUiState.Empty
            NonContentOutcome.Error -> HistoryUiState.Error
            null -> {
                if (usableSnapshot != null) {
                    HistoryUiState.Empty
                } else {
                    HistoryUiState.Loading
                }
            }
        }
    }

    companion object {
        const val CURRENCY_CODE_KEY: String = "currencyCode"
    }
}
