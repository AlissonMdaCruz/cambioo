package com.amdcruz.cambioo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amdcruz.cambioo.data.RatesRepository
import com.amdcruz.cambioo.data.RatesSnapshot
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
class HomeViewModel @Inject constructor(
    private val ratesRepository: RatesRepository,
    private val ratesSessionStore: RatesSessionStore,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = combine(
        ratesRepository.observeSnapshot(),
        ratesSessionStore.session,
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
            ratesSessionStore.refresh(ratesRepository)
        }
    }

    private fun mapUiState(
        snapshot: RatesSnapshot?,
        sessionState: RatesSessionState,
    ): HomeUiState {
        val usable = snapshot?.takeIf { it.hasUsableObservations() }
        if (usable != null) {
            return HomeQuoteMapper.toContent(snapshot = usable, sessionState = sessionState)
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
}
